package com.necron.capitalcraft;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.random.Random;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;
import java.util.*;

public class CapitalCraft implements ModInitializer {
 public static final String MODID="capitalcraft";
 public static final Identifier ACTION = new Identifier(MODID,"action");
 public static final Item BROKER_TABLET = Registry.register(Registries.ITEM, id("broker_tablet"), new MenuItem(new Item.Settings().maxCount(1), "market"));
 public static final Item BET_SLIP = Registry.register(Registries.ITEM, id("bet_slip"), new MenuItem(new Item.Settings().maxCount(1), "casino"));
 public static final Block GPU_RIG = Registry.register(Registries.BLOCK, id("gpu_rig"), new Block(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).strength(3.5f).requiresTool()));
 public static final Item GPU_RIG_ITEM = Registry.register(Registries.ITEM, id("gpu_rig"), new BlockItem(GPU_RIG, new Item.Settings()));
 public static Identifier id(String path){ return new Identifier(MODID,path); }
 @Override public void onInitialize(){
  net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.modifyEntriesEvent(net.minecraft.item.ItemGroups.TOOLS).register(entries -> { entries.add(BROKER_TABLET); entries.add(BET_SLIP); entries.add(GPU_RIG_ITEM); });
  ServerPlayNetworking.registerGlobalReceiver(ACTION,(server,player,handler,buf,responseSender)->{
   String action=buf.readString(64); int amount=buf.readInt();
   server.execute(()->Economy.perform(player,action,amount));
  });
  ServerTickEvents.END_WORLD_TICK.register(world->{ if(!world.isClient && world.getTime()%24000L==0L){ for(ServerPlayerEntity p:world.getPlayers()) Economy.newShift(p); } });
 }
 public static class MenuItem extends Item {
  private final String menu;
  public MenuItem(Settings settings,String menu){super(settings);this.menu=menu;}
  @Override public TypedActionResult<ItemStack> use(World world,PlayerEntity user,Hand hand){
   ItemStack stack=user.getStackInHand(hand);
   if(!world.isClient) user.sendMessage(Text.literal("§6CapitalCraft §7— terminal listo."),true);
   return TypedActionResult.success(stack,world.isClient);
  }
 }
 public static class Economy {
  static final String[] JOBS={"Tala 16 troncos","Mina 24 bloques de piedra","Cultiva 20 cultivos","Pesca 5 peces","Entrega 12 lingotes de hierro","Construye una casa pequeña","Consigue 8 diamantes","Fabrica 32 antorchas","Mata 6 zombis","Explora 500 bloques","Consigue 16 carbón","Cocina 12 alimentos","Recolecta 20 arena","Mina 10 bloques de cobre","Cría 4 animales","Consigue 12 lana","Fabrica 8 herramientas","Reúne 32 tierra","Planta 12 árboles","Consigue 6 perlas de ender","Mina 16 redstone","Recolecta 12 flores","Haz una granja funcional","Encuentra una aldea","Consigue 8 lapislázuli","Fabrica 16 raíles","Consigue 4 obsidianas","Recolecta 16 cañas de azúcar","Haz 3 mapas","Consigue 12 huesos","Mina 20 deepslate","Recolecta 16 bayas dulces","Fabrica 4 escudos","Consigue 10 huevos","Haz 8 panes","Consigue 16 grava","Pesca un tesoro","Consigue 6 bloques de oro","Recolecta 24 hojas","Fabrica 12 ladrillos","Explora una cueva","Consigue 10 slimeballs","Consigue 16 flechas","Construye un puente","Consigue 8 cuarzos","Recolecta 20 cactus","Fabrica 6 libros","Consigue 12 cuero","Mina 32 bloques de andesita","Consigue 4 tótems (bonus)","Entrega 16 troncos de abedul","Fabrica una mesa de encantamientos","Consigue 8 prismarina","Activa un portal del Nether","Entrega 16 alimentos cocinados"};
  static State state(ServerPlayerEntity p){return p.getServer().getOverworld().getPersistentStateManager().getOrCreate(State::fromNbt,State::new,"capitalcraft_economy");}
  public static void perform(ServerPlayerEntity p,String action,int amount){ State s=state(p); String id=p.getUuidAsString(); int cash=s.cash.getOrDefault(id,250); amount=MathHelper.clamp(amount,1,100000);
   switch(action){
    case "bet": if(cash<amount){p.sendMessage(Text.literal("§cCapital insuficiente."),false);return;} cash-=amount; boolean win=p.getRandom().nextDouble()<0.46; int delta=win?amount*2:0; cash+=delta; p.sendMessage(Text.literal(win?"§aAPUESTA GANADA §7+"+delta+" C$: saldo "+cash:"§cAPUESTA PERDIDA §7-"+amount+" C$: saldo "+cash),false); break;
    case "buy": if(cash<amount){p.sendMessage(Text.literal("§cNo tienes suficiente capital."),false);return;} cash-=amount; s.shares.put(id,s.shares.getOrDefault(id,0)+amount); p.sendMessage(Text.literal("§bCompraste "+amount+" participaciones del índice MINE. Usa Vender para liquidar."),false); break;
    case "sell": int shares=s.shares.getOrDefault(id,0); if(shares<=0){p.sendMessage(Text.literal("§cNo tienes participaciones."),false);return;} int market=(int)(shares*(0.55+p.getRandom().nextDouble()*1.0)); cash+=market; s.shares.remove(id); p.sendMessage(Text.literal("§bVenta ejecutada: recibes "+market+" C$."),false); break;
    case "mine": int rigs=s.rigs.getOrDefault(id,0); int cost=100; if(cash<cost){p.sendMessage(Text.literal("§cNecesitas 100 C$ para activar el minado simulado."),false);return;} cash-=cost; int mined=20+p.getRandom().nextInt(101)+rigs*5; cash+=mined; p.sendMessage(Text.literal("§dHash completado: +"+mined+" C$ · energía y volatilidad incluidas."),false); break;
    case "job": String job=JOBS[p.getRandom().nextInt(JOBS.length)]; s.jobs.put(id,job); p.sendMessage(Text.literal("§6JORNADA ASIGNADA §f"+job+" §7· Recompensa: 75 C$"),false); break;
    case "pay": cash+=75; p.sendMessage(Text.literal("§aJornada liquidada: +75 C$. Se reservan comida y herramientas básicas; el resto es impuesto."),false); break;
   }
   s.cash.put(id,cash); s.markDirty(); p.sendMessage(Text.literal("§6CapitalCraft §7Saldo: §f"+cash+" C$"),true);
  }
  static void newShift(ServerPlayerEntity p){State s=state(p); String id=p.getUuidAsString(); s.jobs.put(id,JOBS[p.getRandom().nextInt(JOBS.length)]); s.markDirty(); p.sendMessage(Text.literal("§6Nueva jornada: §f"+s.jobs.get(id)+" §7· usa tu terminal para gestionar tu economía."),false);}
 }
 public static class State extends PersistentState {
  Map<String,Integer> cash=new HashMap<>(), shares=new HashMap<>(), rigs=new HashMap<>(); Map<String,String> jobs=new HashMap<>();
  @Override public NbtCompound writeNbt(NbtCompound n){ NbtCompound c=new NbtCompound(); cash.forEach(c::putInt); n.put("cash",c); NbtCompound sh=new NbtCompound();shares.forEach(sh::putInt);n.put("shares",sh);NbtCompound j=new NbtCompound();jobs.forEach(j::putString);n.put("jobs",j);return n; }
  static State fromNbt(NbtCompound n){State s=new State(); if(n.contains("cash")){NbtCompound c=n.getCompound("cash");for(String k:c.getKeys())s.cash.put(k,c.getInt(k));}if(n.contains("shares")){NbtCompound c=n.getCompound("shares");for(String k:c.getKeys())s.shares.put(k,c.getInt(k));}if(n.contains("jobs")){NbtCompound c=n.getCompound("jobs");for(String k:c.getKeys())s.jobs.put(k,c.getString(k));}return s;}
 }
}
