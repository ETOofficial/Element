package dynastxu.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class SimpleChineseLangProvider extends FabricLanguageProvider {
    private static final String languageCode = "zh_cn";
    public SimpleChineseLangProvider(FabricDataOutput dataOutput) {
        super(dataOutput, languageCode);
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        TranslationBuildHelper helper = new TranslationBuildHelper(translationBuilder, languageCode);

        helper.potionTranslationBuildHelper("instant_hydro", "水元素");
        helper.potionTranslationBuildHelper("instant_pyro", "火元素");
        helper.potionTranslationBuildHelper("instant_anemo", "风元素");
        helper.potionTranslationBuildHelper("instant_electro", "雷元素");
        helper.potionTranslationBuildHelper("instant_cryo", "冰元素");
        helper.potionTranslationBuildHelper("instant_dendro", "草元素");
        helper.potionTranslationBuildHelper("instant_geo", "岩元素");

        helper.potionTranslationBuildHelper("instant_hydro_ii", "水元素", 2);
        helper.potionTranslationBuildHelper("instant_pyro_ii", "火元素", 2);
        helper.potionTranslationBuildHelper("instant_anemo_ii", "风元素", 2);
        helper.potionTranslationBuildHelper("instant_electro_ii", "雷元素", 2);
        helper.potionTranslationBuildHelper("instant_cryo_ii", "冰元素", 2);
        helper.potionTranslationBuildHelper("instant_dendro_ii", "草元素", 2);
        helper.potionTranslationBuildHelper("instant_geo_ii", "岩元素", 2);

        helper.effectTranslationBuildHelper("instant_hydro", "水元素");
        helper.effectTranslationBuildHelper("instant_pyro", "火元素");
        helper.effectTranslationBuildHelper("instant_anemo", "风元素");
        helper.effectTranslationBuildHelper("instant_electro", "雷元素");
        helper.effectTranslationBuildHelper("instant_cryo", "冰元素");
        helper.effectTranslationBuildHelper("instant_dendro", "草元素");
        helper.effectTranslationBuildHelper("instant_geo", "岩元素");

        translationBuilder.add("element.vaporize", "蒸发");
        translationBuilder.add("element.melt", "融化");
        translationBuilder.add("element.overload", "超载");
        translationBuilder.add("element.superconduct", "超导");
        translationBuilder.add("element.electro_charged", "感电");
        translationBuilder.add("element.swirl", "扩散");
        translationBuilder.add("element.crystallize", "结晶");
        translationBuilder.add("element.frozen", "冻结");
        translationBuilder.add("element.bloom", "绽放");
        translationBuilder.add("element.hyperbloom", "超绽放");
        translationBuilder.add("element.burgeon", "烈绽放");
        translationBuilder.add("element.catalyze", "激化");
        translationBuilder.add("element.quicken", "原激化");
        translationBuilder.add("element.aggravate", "超激化");
        translationBuilder.add("element.spread", "蔓激化");

        translationBuilder.add("death.attack.element_damage", "%1$s见证了元素之力");
        translationBuilder.add("death.attack.element_damage.player", "%1$s见证了%2$s的元素之力");
        translationBuilder.add("death.attack.element_damage.item", "%1$s见证了%2$s的%3$s所蕴含的元素之力");
    }
}
