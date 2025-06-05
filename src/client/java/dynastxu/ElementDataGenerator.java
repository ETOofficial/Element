package dynastxu;

import dynastxu.datagen.EnglishLangProvider;
import dynastxu.datagen.SimpleChineseLangProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class ElementDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		// 注册语言提供者
		pack.addProvider(EnglishLangProvider::new);
		pack.addProvider(SimpleChineseLangProvider::new);
	}
}
