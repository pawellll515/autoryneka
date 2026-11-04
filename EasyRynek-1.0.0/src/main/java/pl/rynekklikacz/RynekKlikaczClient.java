package pl.rynekklikacz;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.ClientStopping;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents.Game;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Disconnect;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AfterInit;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AfterRender;
import net.minecraft.class_2561;
import net.minecraft.class_304;
import net.minecraft.class_4185;
import net.minecraft.class_465;
import net.minecraft.class_3675.class_307;
import pl.rynekklikacz.gui.BazaScreen;
import pl.rynekklikacz.mixin.HandledScreenAccessor;

public class RynekKlikaczClient implements ClientModInitializer {
   public static class_304 baza;

   public void onInitializeClient() {
      baza = KeyBindingHelper.registerKeyBinding(new class_304("key.rynekklikacz.baza", class_307.field_1668, 88, "category.rynekklikacz"));
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)client -> {
         while (baza.method_1436()) {
            if (client.field_1755 == null) {
               client.method_1507(new BazaScreen());
            }
         }

         Skaner.skanuj(client);
         Snajper.tick(client);
         Wystawiacz.tick(client);
         Skaner.tick();
      });
      ScreenEvents.AFTER_INIT
         .register(
            (AfterInit)(client, screen, w, h) -> {
               if (screen instanceof class_465) {
                  ScreenEvents.afterRender(screen).register((AfterRender)(s, ctx, mx, my, delta) -> Snajper.tick(client));
               }

               if (Klikacz.rynek(screen)) {
                  HandledScreenAccessor a = (HandledScreenAccessor)screen;
                  int x = a.rynekklikacz$getX();
                  int y = Math.max(2, a.rynekklikacz$getY() - 22);
                  class_4185 start = class_4185.method_46430(class_2561.method_43470("Start"), b -> {
                     Sortowanie.start(client);
                     Klikacz.start();
                  }).method_46434(x, y, 50, 20).method_46431();
                  class_4185 stop = class_4185.method_46430(class_2561.method_43470("Stop"), b -> {
                     Klikacz.stop();
                     Snajper.stop();
                     Skanowanie.stop();
                  }).method_46434(x + 54, y, 50, 20).method_46431();
                  class_4185 skanuj = class_4185.method_46430(class_2561.method_43470("Skanuj"), b -> Skanowanie.start())
                     .method_46434(x + 108, y, 50, 20)
                     .method_46431();
                  class_4185 kalibruj = class_4185.method_46430(class_2561.method_43470("Kalibruj"), b -> Kalibracja.start())
                     .method_46434(x + 162, y, 64, 20)
                     .method_46431();
                  Screens.getButtons(screen).add(start);
                  Screens.getButtons(screen).add(stop);
                  Screens.getButtons(screen).add(skanuj);
                  Screens.getButtons(screen).add(kalibruj);
                  ScreenEvents.afterRender(screen)
                     .register(
                        (AfterRender)(s, ctx, mx, my, delta) -> {
                           Skaner.skanuj(client);
                           start.field_22763 = !Klikacz.dziala();
                           stop.field_22763 = Klikacz.dziala() || Skanowanie.dziala();
                           skanuj.field_22763 = !Skanowanie.dziala();
                           String txt = (
                                 !Skanowanie.dziala() && (Skanowanie.status().isEmpty() || Klikacz.dziala())
                                    ? (
                                       Klikacz.dziala()
                                          ? "§aSkanuje · " + Klikacz.czasCyklu() + " ms §8(L " + Klikacz.lewe() + " · P " + Klikacz.prawe() + ")"
                                          : "§7Zatrzymany"
                                    )
                                    : Skanowanie.status()
                              )
                              + " §8| §7Baza: §f"
                              + Skaner.ile();
                           if (!Snajper.cele().isEmpty()) {
                              txt = txt + " §8| §6Snajper: §f" + Snajper.cele().size() + (Snajper.kupione() > 0 ? " §8· §akupiono " + Snajper.kupione() : "");
                           }

                           ctx.method_25303(client.field_1772, txt, x, y + 23, -1);
                           if (!Kalibracja.status().isEmpty()) {
                              ctx.method_25303(client.field_1772, Kalibracja.status(), x, y - 11, -1);
                           }
                           if (!Sortowanie.status().isEmpty()) {
                              ctx.method_25303(client.field_1772, Sortowanie.status(), x, y - 11, -1);
                           } else if (!Snajper.status.isEmpty() && Klikacz.dziala()) {
                              ctx.method_25303(client.field_1772, (Snajper.zajety() ? "§e" : "§7") + Snajper.status, x, y - 11, -1);
                           }

                           Sortowanie.rysuj(client, s, ctx);
                        }
                     );
               }
            }
         );
      ClientReceiveMessageEvents.GAME.register((Game)(message, overlay) -> Snajper.czat(message));
      ClientPlayConnectionEvents.DISCONNECT.register((Disconnect)(handler, client) -> {
         Klikacz.stop();
         Snajper.stop();
         Skanowanie.stop();
         Skaner.zapisz();
         Historia.zapisz();
      });
      ClientLifecycleEvents.CLIENT_STOPPING.register((ClientStopping)client -> {
         Skaner.zapiszTeraz();
         Historia.zapiszTeraz();
      });
   }
}
