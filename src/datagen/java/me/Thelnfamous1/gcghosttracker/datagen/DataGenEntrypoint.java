package me.Thelnfamous1.gcghosttracker.datagen;

import me.Thelnfamous1.gcghosttracker.GCGhostTracker;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GCGhostTracker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenEntrypoint {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        // Example of adding a datagen provider for blockstates
        // generator.addProvider(event.includeClient(), new MyBlockStateProvider(generator.getPackOutput()));

        BlockTagsProvider blockTagsProvider = new BlockTagsProvider(generator.getPackOutput(), event.getLookupProvider(), GCGhostTracker.MODID, existingFileHelper) {
            @Override
            protected void addTags(HolderLookup.Provider pProvider) {

            }
        };
        generator.addProvider(event.includeServer(), blockTagsProvider);
        generator.addProvider(event.includeServer(), new ItemTagsProvider(generator.getPackOutput(), event.getLookupProvider(), blockTagsProvider.contentsGetter(), GCGhostTracker.MODID, existingFileHelper) {
            @Override
            protected void addTags(HolderLookup.Provider pProvider) {
                this.tag(ItemTags.COMPASSES).add(GCGhostTracker.PLAYER_TRACKER_COMPASS.get());
            }
        });

        generator.addProvider(event.includeClient(), new LanguageProvider(generator.getPackOutput(), GCGhostTracker.MODID, "en_us") {
            @Override
            protected void addTranslations() {
                this.add(GCGhostTracker.PLAYER_TRACKER_COMPASS.get(), "Player Tracker Compass");
                this.add(GCGhostTracker.PLAYER_TRACKER_COMPASS.get().getDescriptionId() + ".tracking", "Tracking");
            }
        });
        generator.addProvider(event.includeClient(), new ItemModelProvider(generator.getPackOutput(), GCGhostTracker.MODID, existingFileHelper) {
            @Override
            protected void registerModels() {

            }

        });
    }
}