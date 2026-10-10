package com.necron.capitalcraft.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class CapitalScreen extends Screen {
 private final String mode;
 private int stake=25;
 public CapitalScreen(String mode){super(Text.literal("CAPITALCRAFT // FINANCIAL DISTRICT"));this.mode=mode;}
 @Override protected void init(){
  int panelW=310, left=(width-panelW)/2, top=Math.max(22,(height-210)/2);
  if(mode.equals("casino")){
   addDrawableChild(ButtonWidget.builder(Text.literal("APOSTAR 25 C$"),b->CapitalCraftClient.send("bet",25)).dimensions(left+20,top+74,130,24).build());
   addDrawableChild(ButtonWidget.builder(Text.literal("APOSTAR 100 C$"),b->CapitalCraftClient.send("bet",100)).dimensions(left+160,top+74,130,24).build());
   addDrawableChild(ButtonWidget.builder(Text.literal("APOSTAR 500 C$"),b->CapitalCraftClient.send("bet",500)).dimensions(left+20,top+104,130,24).build());
   addDrawableChild(ButtonWidget.builder(Text.literal("NUEVA JORNADA"),b->CapitalCraftClient.send("job",0)).dimensions(left+160,top+104,130,24).build());
  } else if(mode.equals("market")){
   addDrawableChild(ButtonWidget.builder(Text.literal("COMPRAR ÍNDICE · 100 C$"),b->CapitalCraftClient.send("buy",100)).dimensions(left+20,top+74,270,24).build());
   addDrawableChild(ButtonWidget.builder(Text.literal("VENDER CARTERA"),b->CapitalCraftClient.send("sell",0)).dimensions(left+20,top+104,270,24).build());
   addDrawableChild(ButtonWidget.builder(Text.literal("ASIGNAR TRABAJO DIARIO"),b->CapitalCraftClient.send("job",0)).dimensions(left+20,top+134,270,24).build());
   addDrawableChild(ButtonWidget.builder(Text.literal("LIQUIDAR JORNADA +75 C$"),b->CapitalCraftClient.send("pay",0)).dimensions(left+20,top+164,270,22).build());
  } else {
   addDrawableChild(ButtonWidget.builder(Text.literal("MINAR CRIPTO · 100 C$"),b->CapitalCraftClient.send("mine",100)).dimensions(left+20,top+74,270,24).build());
   addDrawableChild(ButtonWidget.builder(Text.literal("ASIGNAR TRABAJO DIARIO"),b->CapitalCraftClient.send("job",0)).dimensions(left+20,top+104,270,24).build());
  }
  addDrawableChild(ButtonWidget.builder(Text.literal("SALIR"),b->close()).dimensions(left+20,top+190,270,22).build());
 }
 @Override public void render(DrawContext ctx,int mouseX,int mouseY,float delta){
  renderBackground(ctx);
  int w=330,h=226,x=(width-w)/2,y=(height-h)/2;
  ctx.fill(x-2,y-2,x+w+2,y+h+2,0xffb9a36a);ctx.fill(x,y,x+w,y+h,0xff101923);
  ctx.fill(x,y,x+w,y+42,0xff1c2d3a);ctx.fill(x+12,y+12,x+16,y+30,0xffe0bd68);
  String heading=mode.equals("casino")?"CASINO / RISK DESK":mode.equals("market")?"EXCHANGE / TRADING FLOOR":"CRYPTO / COMPUTE EXCHANGE";
  ctx.drawText(textRenderer,heading,x+25,y+12,0xfff3d99a,false);
  ctx.drawText(textRenderer,"CAPITALCRAFT  •  SIMULACIÓN OFFLINE",x+18,y+51,0xff8fa6b4,false);
  if(mode.equals("casino")){ctx.drawText(textRenderer,"Probabilidad: 46% · pago: 2×",x+18,y+64,0xffe7e7e7,false);ctx.drawText(textRenderer,"La casa siempre cobra su margen.",x+18,y+143,0xff9aabb7,false);}
  else if(mode.equals("market")){ctx.drawText(textRenderer,"Índice MINE · volatilidad simulada",x+18,y+64,0xffe7e7e7,false);ctx.drawText(textRenderer,"Compra y vende sin dinero real.",x+18,y+174,0xff9aabb7,false);}
  else {ctx.drawText(textRenderer,"Hashrate variable · costes de energía",x+18,y+64,0xffe7e7e7,false);ctx.drawText(textRenderer,"Cada operación usa capital del juego.",x+18,y+143,0xff9aabb7,false);}
  super.render(ctx,mouseX,mouseY,delta);
 }
 @Override public boolean shouldPause(){return false;}
}
