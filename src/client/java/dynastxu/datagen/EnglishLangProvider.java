package dynastxu.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.item.Item;

import static dynastxu.effects.ModEffects.*;

public class EnglishLangProvider extends FabricLanguageProvider {
    private static final String languageCode = "en_us";

    public EnglishLangProvider(FabricDataOutput dataOutput) {
        super(dataOutput, languageCode);
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        TranslationBuildHelper helper = new TranslationBuildHelper(translationBuilder, languageCode);

        helper.potionTranslationBuildHelper("instant_hydro", "Hydro");
        helper.potionTranslationBuildHelper("instant_pyro", "Pyro");
        helper.potionTranslationBuildHelper("instant_anemo", "Anemo");
        helper.potionTranslationBuildHelper("instant_electro", "Electro");
        helper.potionTranslationBuildHelper("instant_cryo", "Cryo");
        helper.potionTranslationBuildHelper("instant_dendro", "Dendro");
        helper.potionTranslationBuildHelper("instant_geo", "Geo");

        helper.potionTranslationBuildHelper("instant_hydro_ii", "Hydro", 2);
        helper.potionTranslationBuildHelper("instant_pyro_ii", "Pyro", 2);
        helper.potionTranslationBuildHelper("instant_anemo_ii", "Anemo", 2);
        helper.potionTranslationBuildHelper("instant_electro_ii", "Electro", 2);
        helper.potionTranslationBuildHelper("instant_cryo_ii", "Cryo", 2);
        helper.potionTranslationBuildHelper("instant_dendro_ii", "Dendro", 2);
        helper.potionTranslationBuildHelper("instant_geo_ii", "Geo", 2);

        helper.effectTranslationBuildHelper("instant_hydro", "Instant Hydro");
        helper.effectTranslationBuildHelper("instant_pyro", "Instant Pyro");
        helper.effectTranslationBuildHelper("instant_anemo", "Instant Anemo");
        helper.effectTranslationBuildHelper("instant_electro", "Instant Electro");
        helper.effectTranslationBuildHelper("instant_cryo", "Instant Cryo");
        helper.effectTranslationBuildHelper("instant_dendro", "Instant Dendro");
        helper.effectTranslationBuildHelper("instant_geo", "Instant Geo");

        translationBuilder.add("element.vaporize", "Evaporate");
        translationBuilder.add("element.melt", "Melt");
        translationBuilder.add("element.overload", "Overload");
        translationBuilder.add("element.superconduct", "Superconduct");
        translationBuilder.add("element.electro_charged", "Electro-Charged");
        translationBuilder.add("element.swirl", "Swirl");
        translationBuilder.add("element.crystallize", "Crystallize");
        translationBuilder.add("element.frozen", "Frozen");
        translationBuilder.add("element.bloom", "Bloom");
        translationBuilder.add("element.hyperbloom", "Hyperbloom");
        translationBuilder.add("element.burgeon", "Burgeon");
        translationBuilder.add("element.catalyze", "Catalyze");
        translationBuilder.add("element.quicken", "Quicken");
        translationBuilder.add("element.aggravate", "Aggravate");
        translationBuilder.add("element.spread", "Spread");
    }
}
