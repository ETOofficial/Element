package dynastxu.datagen;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider.TranslationBuilder;

public class TranslationBuildHelper {
    private final TranslationBuilder translationBuilder;
    private final String languageCode;

    public TranslationBuildHelper(TranslationBuilder translationBuilder, String languageCode){
        this.translationBuilder = translationBuilder;
        this.languageCode = languageCode;
    }

    public void potionTranslationBuildHelper(String baseName, String name, String endOfPotionName, String endOfSplashPotionName, String endOfLingeringPotionName, String endOfTippedArrowName) {
        translationBuilder.add("item.minecraft.potion.effect." + baseName, name + endOfPotionName);
        translationBuilder.add("item.minecraft.splash_potion.effect." + baseName, name + endOfSplashPotionName);
        translationBuilder.add("item.minecraft.lingering_potion.effect." + baseName, name + endOfLingeringPotionName);
        translationBuilder.add("item.minecraft.tipped_arrow.effect." + baseName, name + endOfTippedArrowName);

    }

    public void potionTranslationBuildHelper(String baseName, String name, int amplifier) {
        String endOfPotionName;
        String endOfSplashPotionName;
        String endOfLingeringPotionName;
        String endOfTippedArrowName;
        String nameOfLevel;
        if (amplifier >= 2) {
            nameOfLevel = " " + "I".repeat(amplifier);
        } else {
            nameOfLevel = "";
        }
        switch (languageCode) {
            case "zh_cn":
                endOfPotionName = "药水" + nameOfLevel;
                endOfSplashPotionName = "喷溅药水" + nameOfLevel;
                endOfLingeringPotionName = "滞留药水" + nameOfLevel;
                endOfTippedArrowName = "之箭";
                break;
            case "en_us":
            default:
                endOfPotionName = " Potion" + nameOfLevel;
                endOfSplashPotionName = " Splash Potion" + nameOfLevel;
                endOfLingeringPotionName = " Lingering Potion" + nameOfLevel;
                endOfTippedArrowName = " Tipped Arrow";
                break;
        }
        potionTranslationBuildHelper(baseName, name, endOfPotionName, endOfSplashPotionName, endOfLingeringPotionName, endOfTippedArrowName);
    }

    public void potionTranslationBuildHelper(String baseName, String name){
        potionTranslationBuildHelper(baseName, name, 1);
    }

    public void effectTranslationBuildHelper(String baseName, String name) {
        translationBuilder.add("effect.element." + baseName, name);
    }
}
