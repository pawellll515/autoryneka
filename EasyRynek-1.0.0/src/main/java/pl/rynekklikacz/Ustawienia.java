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
