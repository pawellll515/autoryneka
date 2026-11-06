package pl.rynekklikacz;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2522;
import net.minecraft.class_310;

public final class Zakupy {
   private static final Gson GSON = new Gson();
   private static List<Zakupy.Zakup> lista;
   private static int wersja;

   private Zakupy() {
   }

   private static Path plik() {
      return FabricLoader.getInstance().getGameDir().resolve("rynekklikacz").resolve("zakupy.json");
   }

   public static List<Zakupy.Zakup> wszystkie() {
      if (lista == null) {
         lista = new ArrayList<>();

         try {
            if (Files.exists(plik())) {
               List<Zakupy.Zakup> l = (List<Zakupy.Zakup>)GSON.fromJson(
                  Files.readString(plik(), StandardCharsets.UTF_8), (new TypeToken<List<Zakupy.Zakup>>() {}).getType()
               );
               if (l != null) {
                  lista.addAll(l);
               }
            }
         } catch (Exception var1) {
         }
      }

      return lista;
   }

   public static int wersja() {
      return wersja;
   }

   public static void dodaj(Zakupy.Zakup z) {
      wszystkie().add(0, z);
      wersja++;
      zapisz();
   }

   public static void wyczysc() {
      wszystkie().clear();
      wersja++;
      zapisz();
   }

   public static void zmieniono() {
      wersja++;
      zapisz();
   }

   private static void zapisz() {
      try {
         Files.createDirectories(plik().getParent());
         Files.writeString(plik(), GSON.toJson(lista), StandardCharsets.UTF_8);
      } catch (Exception var1) {
      }
   }

   public static class Zakup {
      public long czas;
      public String nazwa = "";
      public String id = "";
      public String grupa = "";
      public int ilosc = 1;
      public long cena;
      public double rynkowa;
      public double zysk;
      public int proby;
      public boolean wystawiono;
      public long cenaWystawienia;
      public int iloscWystawienia;
      public long czasWystawienia;
      public boolean naPasku;
      public boolean auto;
      public String item = "";
      private transient class_1799 stack;

      public class_1799 stack() {
         if (this.stack == null) {
            class_310 mc = class_310.method_1551();
            if (mc.field_1687 == null) {
               return new class_1799(class_1802.field_8407);
            }

            this.stack = new class_1799(class_1802.field_8407);

            try {
               this.stack = class_1799.method_57360(mc.field_1687.method_30349(), class_2522.method_10718(this.item)).orElse(this.stack);
            } catch (Exception var3) {
            }
         }

         return this.stack;
      }
   }
}
