package com.hyd.pipes_bakery_backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hyd.pipes_bakery_backend.config.ImageProperties;
import com.hyd.pipes_bakery_backend.customcake.CakeColor;
import com.hyd.pipes_bakery_backend.customcake.CakeExtra;
import com.hyd.pipes_bakery_backend.customcake.CakeFlavour;
import com.hyd.pipes_bakery_backend.customcake.CakeSize;
import com.hyd.pipes_bakery_backend.customcake.CustomCakeCatalog;
import com.hyd.pipes_bakery_backend.dto.customcake.CustomCakeConfigurationDTO;
import com.hyd.pipes_bakery_backend.dto.customcake.CustomCakeDetails;
import com.hyd.pipes_bakery_backend.dto.customcake.CustomCakeOptionsResponseDTO;
import com.hyd.pipes_bakery_backend.exception.InvalidCustomCakeException;

@Service
public class CustomCakeService implements ICustomCakeService {

    public static final String IMAGE_SUBDIRECTORY = "custom-cakes";

    private static final Pattern IMAGE_FILE_PATTERN =
            Pattern.compile("^" + IMAGE_SUBDIRECTORY + "/[A-Za-z0-9_-]+\\.(jpg|png|webp)$");
    private static final Pattern CONTROL_CHARACTERS = Pattern.compile("\\p{Cntrl}");
    private static final Pattern FREE_TEXT_INVALID_CHARACTERS = Pattern.compile("[\\p{Cntrl}&&[^\\n\\r\\t]]");

    private final CustomCakeCatalog catalog;
    private final IImageStorageService imageStorageService;
    private final UploadRateLimitService uploadRateLimitService;
    private final ImageProperties imageProperties;

    public CustomCakeService(CustomCakeCatalog catalog,
                             IImageStorageService imageStorageService,
                             UploadRateLimitService uploadRateLimitService,
                             ImageProperties imageProperties) {
        this.catalog = catalog;
        this.imageStorageService = imageStorageService;
        this.uploadRateLimitService = uploadRateLimitService;
        this.imageProperties = imageProperties;
    }

    @Override
    public CustomCakeOptionsResponseDTO getOptions() {
        return new CustomCakeOptionsResponseDTO(
                catalog.getSizes(),
                catalog.getFlavours(),
                catalog.getColors(),
                catalog.getExtras(),
                CustomCakeCatalog.MAX_DECORATIVE_TIERS,
                CustomCakeCatalog.DECORATIVE_TIER_PRICE,
                CustomCakeCatalog.MAX_TEXT_LENGTH,
                CustomCakeCatalog.TEXT_PRICE,
                CustomCakeCatalog.IMAGE_PRICE,
                CustomCakeCatalog.MAX_NOTES_LENGTH,
                CustomCakeCatalog.MAX_DIETARY_LENGTH
        );
    }

    @Override
    public CustomCakeDetails resolve(CustomCakeConfigurationDTO configuration) {
        if (configuration == null) {
            throw new InvalidCustomCakeException("Cake configuration is required");
        }

        CakeSize size = catalog.findSize(configuration.getSizeId())
                .orElseThrow(() -> new InvalidCustomCakeException("Unknown cake size"));
        CakeFlavour flavour = catalog.findFlavour(configuration.getFlavourId())
                .orElseThrow(() -> new InvalidCustomCakeException("Unknown cake flavour"));
        CakeColor color = catalog.findColor(configuration.getColorId())
                .orElseThrow(() -> new InvalidCustomCakeException("Unknown cake color"));

        int decorativeTiers = configuration.getDecorativeTiers();
        if (decorativeTiers < 0 || decorativeTiers > CustomCakeCatalog.MAX_DECORATIVE_TIERS) {
            throw new InvalidCustomCakeException(
                    "Decorative tiers must be between 0 and " + CustomCakeCatalog.MAX_DECORATIVE_TIERS);
        }

        // Allergies matter for a bakery: the customer must answer, and name them if they have any
        if (configuration.getHasDietaryRestrictions() == null) {
            throw new InvalidCustomCakeException("Please tell us whether there are dietary restrictions");
        }
        boolean hasDietaryRestrictions = configuration.getHasDietaryRestrictions();
        String dietaryRestrictions = null;
        if (hasDietaryRestrictions) {
            dietaryRestrictions = normalizeFreeText(configuration.getDietaryRestrictions(),
                    CustomCakeCatalog.MAX_DIETARY_LENGTH, "Dietary restrictions");
            if (dietaryRestrictions == null) {
                throw new InvalidCustomCakeException("Please specify the dietary restrictions");
            }
        }

        List<CakeExtra> extras = resolveExtras(configuration.getExtraIds());

        CustomCakeDetails details = new CustomCakeDetails();
        details.setHasDietaryRestrictions(hasDietaryRestrictions);
        details.setDietaryRestrictions(dietaryRestrictions);
        details.setSizeId(size.id());
        details.setSizeLabel(size.label());
        details.setSizeDescription(size.description());
        details.setServings(size.servings());
        details.setTierDiameters(size.tierDiameters());
        details.setFlavourId(flavour.id());
        details.setFlavourLabel(flavour.label());
        details.setDecorativeTiers(decorativeTiers);
        details.setColorId(color.id());
        details.setColorLabel(color.label());
        details.setColorHex(color.hex());
        details.setText(normalizeText(configuration.getText()));
        details.setImageFile(validateImageFile(configuration.getImageFile()));
        details.setExtraIds(extras.stream().map(CakeExtra::id).toList());
        details.setExtraLabels(extras.stream().map(CakeExtra::label).toList());
        details.setNotes(normalizeFreeText(configuration.getNotes(), CustomCakeCatalog.MAX_NOTES_LENGTH, "Notes"));
        return details;
    }

    @Override
    public BigDecimal price(CustomCakeDetails details) {
        CakeSize size = catalog.findSize(details.getSizeId())
                .orElseThrow(() -> new InvalidCustomCakeException("Unknown cake size"));
        CakeFlavour flavour = catalog.findFlavour(details.getFlavourId())
                .orElseThrow(() -> new InvalidCustomCakeException("Unknown cake flavour"));

        BigDecimal total = size.basePrice()
                .add(CustomCakeCatalog.DECORATIVE_TIER_PRICE.multiply(BigDecimal.valueOf(details.getDecorativeTiers())))
                .add(flavour.price());

        if (details.getText() != null) {
            total = total.add(CustomCakeCatalog.TEXT_PRICE);
        }
        if (details.getImageFile() != null) {
            total = total.add(CustomCakeCatalog.IMAGE_PRICE);
        }
        for (String extraId : details.getExtraIds()) {
            CakeExtra extra = catalog.findExtra(extraId)
                    .orElseThrow(() -> new InvalidCustomCakeException("Unknown decorative element"));
            total = total.add(extra.price());
        }

        return total.setScale(0, RoundingMode.HALF_UP);
    }

    @Override
    public String storeImage(MultipartFile file, String clientIp) {
        uploadRateLimitService.assertUploadAllowed(clientIp);
        return imageStorageService.store(file, IMAGE_SUBDIRECTORY, "cake");
    }

    private List<CakeExtra> resolveExtras(List<String> extraIds) {
        List<CakeExtra> extras = new ArrayList<>();
        if (extraIds == null) {
            return extras;
        }

        LinkedHashSet<String> uniqueIds = new LinkedHashSet<>(extraIds);
        if (uniqueIds.size() != extraIds.size()) {
            throw new InvalidCustomCakeException("Decorative elements must not be repeated");
        }

        for (String extraId : uniqueIds) {
            extras.add(catalog.findExtra(extraId)
                    .orElseThrow(() -> new InvalidCustomCakeException("Unknown decorative element")));
        }
        return extras;
    }

    private String normalizeText(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        String trimmed = text.trim();
        if (CONTROL_CHARACTERS.matcher(trimmed).find()) {
            throw new InvalidCustomCakeException("Cake text contains invalid characters");
        }
        if (trimmed.codePointCount(0, trimmed.length()) > CustomCakeCatalog.MAX_TEXT_LENGTH) {
            throw new InvalidCustomCakeException(
                    "Cake text must have at most " + CustomCakeCatalog.MAX_TEXT_LENGTH + " characters");
        }
        return trimmed;
    }

    // Multi-line free text (notes, dietary restrictions): line breaks are fine, other control characters are not
    private String normalizeFreeText(String value, int maxLength, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String trimmed = value.strip();
        if (FREE_TEXT_INVALID_CHARACTERS.matcher(trimmed).find()) {
            throw new InvalidCustomCakeException(fieldName + " contain invalid characters");
        }
        if (trimmed.codePointCount(0, trimmed.length()) > maxLength) {
            throw new InvalidCustomCakeException(fieldName + " must have at most " + maxLength + " characters");
        }
        return trimmed;
    }

    private String validateImageFile(String imageFile) {
        if (imageFile == null || imageFile.isBlank()) {
            return null;
        }

        if (!IMAGE_FILE_PATTERN.matcher(imageFile).matches()) {
            throw new InvalidCustomCakeException("Invalid cake image");
        }

        Path directory = Paths.get(imageProperties.getPath()).toAbsolutePath().normalize();
        Path image = directory.resolve(imageFile).normalize();
        if (!image.startsWith(directory) || !Files.isRegularFile(image)) {
            throw new InvalidCustomCakeException("Cake image not found. Please upload it again");
        }
        return imageFile;
    }
}
