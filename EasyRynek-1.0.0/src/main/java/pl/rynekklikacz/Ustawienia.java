package pl.rynekklikacz;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public final class Ustawienia {
   public static final int MIN_PROFIT = 1;
   public static final int MAX_PROFIT = 200;
   private static int profit = -1;
   private static boolean autoWystaw = true;
   private static boolean autoPowrot = true;
   private static int nastepnaStronaSlot = -1;
   private static int nastepnaStronaButton = 0;
   private static int poprzedniaStronaSlot = -1;
   private static int poprzedniaStronaButton = 0;

   private Ustawienia() {
   }

   private static Path plik() {
      return FabricLoader.getInstance().getGameDir().resolve("rynekklikacz").resolve("ustawienia.json");
   }

   private static void wczytaj() {
      if (profit < 0) {
         profit = 20;

         try {
            if (Files.exists(plik())) {
               JsonObject o = (JsonObject)new Gson().fromJson(Files.readString(plik(), StandardCharsets.UTF_8), JsonObject.class);
               if (o != null && o.has("profit")) {
                  profit = o.get("profit").getAsInt();
               }

               if (o != null && o.has("autoWystaw")) {
                  autoWystaw = o.get("autoWystaw").getAsBoolean();
               }

               if (o != null && o.has("autoPowrot")) {
                  autoPowrot = o.get("autoPowrot").getAsBoolean();
               }

               if (o != null && o.has("nastepnaStronaSlot")) {
                  nastepnaStronaSlot = o.get("nastepnaStronaSlot").getAsInt();
               }
               if (o != null && o.has("nastepnaStronaButton")) {
                  nastepnaStronaButton = o.get("nastepnaStronaButton").getAsInt();
               }
               if (o != null && o.has("poprzedniaStronaSlot")) {
                  poprzedniaStronaSlot = o.get("poprzedniaStronaSlot").getAsInt();
               }
               if (o != null && o.has("poprzedniaStronaButton")) {
                  poprzedniaStronaButton = o.get("poprzedniaStronaButton").getAsInt();
               }
            }
         } catch (Exception var1) {
         }

         profit = Math.max(1, Math.min(200, profit));
      }
   }

   private static void zapisz() {
      try {
         Files.createDirectories(plik().getParent());
         JsonObject o = new JsonObject();
         o.addProperty("profit", profit);
         o.addProperty("autoWystaw", autoWystaw);
         o.addProperty("autoPowrot", autoPowrot);
         o.addProperty("nastepnaStronaSlot", nastepnaStronaSlot);
         o.addProperty("nastepnaStronaButton", nastepnaStronaButton);
         o.addProperty("poprzedniaStronaSlot", poprzedniaStronaSlot);
         o.addProperty("poprzedniaStronaButton", poprzedniaStronaButton);
         Files.writeString(plik(), o.toString(), StandardCharsets.UTF_8);
      } catch (Exception var1) {
      }
   }

   public static int profit() {
      wczytaj();
      return profit;
   }

   public static void profit(int v) {
      wczytaj();
      profit = Math.max(1, Math.min(200, v));
      zapisz();
   }

   public static int nastepnaStronaSlot() { wczytaj(); return nastepnaStronaSlot; }
   public static int nastepnaStronaButton() { wczytaj(); return nastepnaStronaButton; }
   public static int poprzedniaStronaSlot() { wczytaj(); return poprzedniaStronaSlot; }
   public static int poprzedniaStronaButton() { wczytaj(); return poprzedniaStronaButton; }

   public static void zapiszPrzyciskiStron(int nextSlot, int nextButton, int prevSlot, int prevButton) {
      wczytaj();
      nastepnaStronaSlot = nextSlot;
      nastepnaStronaButton = nextButton;
      poprzedniaStronaSlot = prevSlot;
      poprzedniaStronaButton = prevButton;
      zapisz();
   }

   public static boolean autoWystaw() {
      wczytaj();
      return autoWystaw;
   }

   public static boolean autoPowrot() {
      wczytaj();
      return autoPowrot;
   }

   public static void autoPowrot(boolean v) {
      wczytaj();
      autoPowrot = v;
      zapisz();
   }

   public static void autoWystaw(boolean v) {
      wczytaj();
      autoWystaw = v;
      zapisz();
   }
}
