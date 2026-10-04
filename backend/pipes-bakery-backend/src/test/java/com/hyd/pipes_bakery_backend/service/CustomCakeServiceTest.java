package com.hyd.pipes_bakery_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.hyd.pipes_bakery_backend.config.ImageProperties;
import com.hyd.pipes_bakery_backend.customcake.CustomCakeCatalog;
import com.hyd.pipes_bakery_backend.dto.customcake.CustomCakeConfigurationDTO;
import com.hyd.pipes_bakery_backend.dto.customcake.CustomCakeDetails;
import com.hyd.pipes_bakery_backend.dto.customcake.CustomCakeOptionsResponseDTO;
import com.hyd.pipes_bakery_backend.exception.InvalidCustomCakeException;

@ExtendWith(MockitoExtension.class)
class CustomCakeServiceTest {

    @Mock
    private IImageStorageService imageStorageService;

    @Mock
    private UploadRateLimitService uploadRateLimitService;

    @TempDir
    Path imagesDir;

    private CustomCakeService customCakeService;

    @BeforeEach
    void setUp() {
        ImageProperties imageProperties = new ImageProperties();
        imageProperties.setPath(imagesDir.toString());
        customCakeService = new CustomCakeService(new CustomCakeCatalog(), imageStorageService, uploadRateLimitService, imageProperties);
    }

    @Test
    void shouldExposeTheThreeSizesWithTheirRealTiers() {
        CustomCakeOptionsResponseDTO options = customCakeService.getOptions();

        assertThat(options.sizes()).extracting("id").containsExactly("S", "M", "L");
        assertThat(options.sizes().get(0).tierDiameters()).containsExactly(15);
        assertThat(options.sizes().get(0).servings()).isEqualTo(8);
        assertThat(options.sizes().get(1).tierDiameters()).containsExactly(20);
        assertThat(options.sizes().get(1).servings()).isEqualTo(16);
        assertThat(options.sizes().get(2).tierDiameters()).containsExactly(20, 15);
        assertThat(options.sizes().get(2).description()).isEqualTo("2 pisos de 20 y 15 cm");
        assertThat(options.sizes().get(2).servings()).isEqualTo(24);
        assertThat(options.maxDecorativeTiers()).isEqualTo(2);
        assertThat(options.maxTextLength()).isEqualTo(40);
    }

    @Test
    void shouldResolveAValidConfigurationWithLabels() {
        CustomCakeConfigurationDTO config = config("L", "red-velvet", 2, "fresa");
        config.setText("  Feliz cumpleaños Ana  ");
        config.setExtraIds(List.of("chispas", "velas"));
        config.setNotes("  Letras en dorado,\npor favor  ");

        CustomCakeDetails details = customCakeService.resolve(config);

        assertThat(details.getSizeLabel()).isEqualTo("Grande");
        assertThat(details.getTierDiameters()).containsExactly(20, 15);
        assertThat(details.getFlavourLabel()).isEqualTo("Red velvet");
        assertThat(details.getDecorativeTiers()).isEqualTo(2);
        assertThat(details.getColorLabel()).isEqualTo("Rosa fresa");
        assertThat(details.getText()).isEqualTo("Feliz cumpleaños Ana");
        assertThat(details.getExtraLabels()).containsExactly("Chispas", "Velas");
        assertThat(details.getNotes()).isEqualTo("Letras en dorado,\npor favor");
        assertThat(details.isHasDietaryRestrictions()).isFalse();
        assertThat(details.getDietaryRestrictions()).isNull();
    }

    @Test
    void shouldPriceFromTheServerCatalog() {
        // M base 140000 + 1 decorative tier 35000 + chocolate 0 + text 8000 + chispas 6000
        CustomCakeConfigurationDTO config = config("M", "chocolate", 1, "chantilly");
        config.setText("Ana");
        config.setExtraIds(List.of("chispas"));

        BigDecimal price = customCakeService.price(customCakeService.resolve(config));

        assertThat(price).isEqualByComparingTo(new BigDecimal("189000"));
    }

    @Test
    void shouldAddThePhotoPrintPrice() throws IOException {
        Files.createDirectories(imagesDir.resolve("custom-cakes"));
        Files.writeString(imagesDir.resolve("custom-cakes/cake-abc123.jpg"), "img");

        CustomCakeConfigurationDTO config = config("S", "vainilla", 0, "chantilly");
        config.setImageFile("custom-cakes/cake-abc123.jpg");

        CustomCakeDetails details = customCakeService.resolve(config);

        assertThat(details.getImageFile()).isEqualTo("custom-cakes/cake-abc123.jpg");
        assertThat(customCakeService.price(details)).isEqualByComparingTo(new BigDecimal("113000"));
    }

    @Test
    void shouldAllowZeroOneOrTwoDecorativeTiersOnAnySize() {
        for (String size : List.of("S", "M", "L")) {
            for (int tiers = 0; tiers <= 2; tiers++) {
                assertThat(customCakeService.resolve(config(size, "vainilla", tiers, "chantilly")).getDecorativeTiers())
                        .isEqualTo(tiers);
            }
        }

        assertThatThrownBy(() -> customCakeService.resolve(config("S", "vainilla", 3, "chantilly")))
                .isInstanceOf(InvalidCustomCakeException.class)
                .hasMessage("Decorative tiers must be between 0 and 2");
        assertThatThrownBy(() -> customCakeService.resolve(config("S", "vainilla", -1, "chantilly")))
                .isInstanceOf(InvalidCustomCakeException.class);
    }

    @Test
    void shouldRequireAnAnswerAboutDietaryRestrictions() {
        CustomCakeConfigurationDTO unanswered = config("M", "vainilla", 0, "chantilly");
        unanswered.setHasDietaryRestrictions(null);
        assertThatThrownBy(() -> customCakeService.resolve(unanswered))
                .isInstanceOf(InvalidCustomCakeException.class)
                .hasMessage("Please tell us whether there are dietary restrictions");

        CustomCakeConfigurationDTO unspecified = config("M", "vainilla", 0, "chantilly");
        unspecified.setHasDietaryRestrictions(true);
        unspecified.setDietaryRestrictions("   ");
        assertThatThrownBy(() -> customCakeService.resolve(unspecified))
                .isInstanceOf(InvalidCustomCakeException.class)
                .hasMessage("Please specify the dietary restrictions");
    }

    @Test
    void shouldKeepTheDietaryRestrictionsOnlyWhenThereAreSome() {
        CustomCakeConfigurationDTO withRestrictions = config("M", "vainilla", 0, "chantilly");
        withRestrictions.setHasDietaryRestrictions(true);
        withRestrictions.setDietaryRestrictions(" Sin gluten, alergia a nueces ");
        CustomCakeDetails details = customCakeService.resolve(withRestrictions);
        assertThat(details.isHasDietaryRestrictions()).isTrue();
        assertThat(details.getDietaryRestrictions()).isEqualTo("Sin gluten, alergia a nueces");

        // Text typed before switching the answer back to "no" is discarded
        CustomCakeConfigurationDTO without = config("M", "vainilla", 0, "chantilly");
        without.setDietaryRestrictions("Sin gluten");
        assertThat(customCakeService.resolve(without).getDietaryRestrictions()).isNull();
    }

    @Test
    void shouldRejectNotesThatAreTooLong() {
        CustomCakeConfigurationDTO config = config("M", "vainilla", 0, "chantilly");
        config.setNotes("a".repeat(501));

        assertThatThrownBy(() -> customCakeService.resolve(config))
                .isInstanceOf(InvalidCustomCakeException.class)
                .hasMessageContaining("at most 500");
    }

    @Test
    void shouldRejectUnknownOptions() {
        assertThatThrownBy(() -> customCakeService.resolve(config("XL", "vainilla", 0, "chantilly")))
                .isInstanceOf(InvalidCustomCakeException.class)
                .hasMessage("Unknown cake size");
        assertThatThrownBy(() -> customCakeService.resolve(config("M", "mango", 0, "chantilly")))
                .isInstanceOf(InvalidCustomCakeException.class)
                .hasMessage("Unknown cake flavour");
        assertThatThrownBy(() -> customCakeService.resolve(config("M", "vainilla", 0, "neon")))
                .isInstanceOf(InvalidCustomCakeException.class)
                .hasMessage("Unknown cake color");

        CustomCakeConfigurationDTO withUnknownExtra = config("M", "vainilla", 0, "chantilly");
        withUnknownExtra.setExtraIds(List.of("fuegos"));
        assertThatThrownBy(() -> customCakeService.resolve(withUnknownExtra))
                .isInstanceOf(InvalidCustomCakeException.class)
                .hasMessage("Unknown decorative element");
    }

    @Test
    void shouldRejectRepeatedExtras() {
        CustomCakeConfigurationDTO config = config("M", "vainilla", 0, "chantilly");
        config.setExtraIds(List.of("velas", "velas"));

        assertThatThrownBy(() -> customCakeService.resolve(config))
                .isInstanceOf(InvalidCustomCakeException.class)
                .hasMessage("Decorative elements must not be repeated");
    }

    @Test
    void shouldRejectTextThatIsTooLongOrHasControlCharacters() {
        CustomCakeConfigurationDTO tooLong = config("M", "vainilla", 0, "chantilly");
        tooLong.setText("a".repeat(41));
        assertThatThrownBy(() -> customCakeService.resolve(tooLong))
                .isInstanceOf(InvalidCustomCakeException.class)
                .hasMessageContaining("at most 40");

        CustomCakeConfigurationDTO control = config("M", "vainilla", 0, "chantilly");
        control.setText("Hola\u0000Ana");
        assertThatThrownBy(() -> customCakeService.resolve(control))
                .isInstanceOf(InvalidCustomCakeException.class)
                .hasMessage("Cake text contains invalid characters");
    }

    @Test
    void shouldTreatBlankTextAsNoText() {
        CustomCakeConfigurationDTO config = config("M", "vainilla", 0, "chantilly");
        config.setText("   ");

        assertThat(customCakeService.resolve(config).getText()).isNull();
    }

    @Test
    void shouldRejectImagesOutsideTheCustomCakeFolderOrMissing() {
        CustomCakeConfigurationDTO traversal = config("M", "vainilla", 0, "chantilly");
        traversal.setImageFile("custom-cakes/../secret.jpg");
        assertThatThrownBy(() -> customCakeService.resolve(traversal))
                .isInstanceOf(InvalidCustomCakeException.class)
                .hasMessage("Invalid cake image");

        CustomCakeConfigurationDTO productImage = config("M", "vainilla", 0, "chantilly");
        productImage.setImageFile("brookie.jpg");
        assertThatThrownBy(() -> customCakeService.resolve(productImage))
                .isInstanceOf(InvalidCustomCakeException.class)
                .hasMessage("Invalid cake image");

        CustomCakeConfigurationDTO missing = config("M", "vainilla", 0, "chantilly");
        missing.setImageFile("custom-cakes/cake-missing.png");
        assertThatThrownBy(() -> customCakeService.resolve(missing))
                .isInstanceOf(InvalidCustomCakeException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void shouldCheckTheRateLimitBeforeStoringAnImage() {
        MockMultipartFile file = new MockMultipartFile("file", "foto.jpg", "image/jpeg", new byte[] {1});
        when(imageStorageService.store(file, "custom-cakes", "cake")).thenReturn("custom-cakes/cake-1.jpg");

        String stored = customCakeService.storeImage(file, "10.0.0.1");

        assertThat(stored).isEqualTo("custom-cakes/cake-1.jpg");
        InOrder order = inOrder(uploadRateLimitService, imageStorageService);
        order.verify(uploadRateLimitService).assertUploadAllowed("10.0.0.1");
        order.verify(imageStorageService).store(file, "custom-cakes", "cake");
    }

    private CustomCakeConfigurationDTO config(String sizeId, String flavourId, int decorativeTiers, String colorId) {
        CustomCakeConfigurationDTO config = new CustomCakeConfigurationDTO();
        config.setHasDietaryRestrictions(false);
        config.setSizeId(sizeId);
        config.setFlavourId(flavourId);
        config.setDecorativeTiers(decorativeTiers);
        config.setColorId(colorId);
        return config;
    }
}
