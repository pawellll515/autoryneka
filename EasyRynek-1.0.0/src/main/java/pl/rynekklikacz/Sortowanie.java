package pl.rynekklikacz;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1109;
import net.minecraft.class_124;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_2813;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_3417;
import net.minecraft.class_437;
import net.minecraft.class_5251;
import net.minecraft.class_634;
import net.minecraft.class_9290;
import net.minecraft.class_9334;
import pl.rynekklikacz.gui.Draw;
import pl.rynekklikacz.mixin.HandledScreenAccessor;

public final class Sortowanie {
   private static final String ZNACZNIKI = "»›>►▶➤➜➥→✔✓●⬤◆✦★•";
   private static long ostrzezenieDo;
   private static long okDo;
   private static int proby;
   private static long klikT;
   private static int syncPrzy;
   private static int podpisPrzy;

   private Sortowanie() {
   }

   private static class_1735 slot(class_310 mc) {
      if (mc.field_1724 == null) {
         return null;
      }

      class_1703 h = mc.field_1724.field_7512;
      int i = h.field_7761.size() - 37;
      return i < 0 ? null : h.method_7611(i);
   }

   public static void start(class_310 mc) {
      class_1735 s = slot(mc);
      zrzut(s == null ? class_1799.field_8037 : s.method_7677());
      ostrzezenieDo = 0L;
      proby = 0;
   }

   public static boolean ustawia() {
      return proby > 0;
   }

   public static String status() {
      if (proby > 0 && System.currentTimeMillis() - klikT > 3000L) {
         proby = 0;
      }

      if (proby > 0) {
         return "§eUstawiam sortowanie na Najnowsze · klik " + proby;
      } else {
         return System.currentTimeMillis() < okDo ? "§a✔ Sortowanie: Najnowsze" : "";
      }
   }

   static boolean naprawia(class_310 mc) {
      class_1735 s = slot(mc);
      class_634 net = mc.method_1562();
      if (s != null && net != null) {
         long t = System.currentTimeMillis();
         if (proby > 0 && t - klikT > 3000L) {
            proby = 0;
         }

         Boolean n = najnowsze(s.method_7677());
         if (Boolean.TRUE.equals(n)) {
            if (proby > 0) {
               proby = 0;
               okDo = t + 2500L;
            }

            return false;
         } else {
            if (n == null) {
               return proby > 0 && t - klikT < 400L;
            }

            class_1703 h = mc.field_1724.field_7512;
            boolean zmienione = h.field_7763 != syncPrzy || class_1799.method_57355(s.method_7677()) != podpisPrzy;
            if (proby <= 0 || t - klikT >= 60L && (zmienione || t - klikT >= 400L)) {
               if (proby >= 8) {
                  proby = 0;
                  Klikacz.stop();
                  Skanowanie.stop();
                  ostrzezenieDo = t + 10000L;
                  mc.method_1483().method_4873(class_1109.method_47978(class_3417.field_14624, 0.7F));
                  return true;
               } else {
                  syncPrzy = h.field_7763;
                  podpisPrzy = class_1799.method_57355(s.method_7677());
                  klikT = t;
                  proby++;
                  net.method_52787(
                     new class_2813(h.field_7763, h.method_37421(), s.field_7874, 0, class_1713.field_7790, class_1799.field_8037, new Int2ObjectOpenHashMap())
                  );
                  return true;
               }
            } else {
               return true;
            }
         }
      } else {
         return false;
      }
   }

   private static String czysty(class_2561 t) {
      return class_124.method_539(t.getString()).trim().toLowerCase(Locale.ROOT);
   }

   private static class_5251 kolor(class_2561 t) {
      String s = t.getString();
      int i = s.indexOf(167);
      if (i >= 0 && i + 1 < s.length()) {
         class_124 f = class_124.method_544(s.charAt(i + 1));
         if (f != null && f.method_543()) {
            return class_5251.method_27718(f);
         }
      }

      class_5251[] k = new class_5251[]{null};
      t.method_27658((styl, tekst) -> {
         if (!tekst.isBlank()) {
            k[0] = styl.method_10973();
            return Optional.of(Boolean.TRUE);
         } else {
            return Optional.empty();
         }
      }, class_2583.field_24360);
      return k[0];
   }

   private static boolean znacznik(String s) {
      return !s.isEmpty() && "»›>►▶➤➜➥→✔✓●⬤◆✦★•".indexOf(s.charAt(0)) >= 0;
   }

   static Boolean najnowsze(class_1799 st) {
      if (st.method_7960()) {
         return null;
      }

      String nazwa = czysty(st.method_7964());
      class_9290 lore = (class_9290)st.method_57824(class_9334.field_49632);
      List<class_2561> linie = new ArrayList<>();
      if (lore != null) {
         for (class_2561 t : lore.comp_2400()) {
            if (!czysty(t).isEmpty()) {
               linie.add(t);
            }
         }
      }

      int n = -1;

      for (int i = 0; i < linie.size(); i++) {
         if (czysty(linie.get(i)).contains("najnowsz")) {
            n = i;
         }
      }

      if (n < 0) {
         if (nazwa.contains("najnowsz")) {
            return true;
         }

         if (nazwa.contains("sort")) {
            return false;
         }

         for (class_2561 t : linie) {
            if (czysty(t).contains("sort")) {
               return false;
            }
         }

         return null;
      } else {
         if (nazwa.contains("najnowsz")) {
            return true;
         }

         for (class_2561 t : linie) {
            String s = czysty(t);
            if (s.matches(".*(aktualn|obecn|wybran|teraz).*") && s.contains(":")) {
               return s.contains("najnowsz");
            }
         }

         int od = n;
         int doo = n;

         while (od > 0 && opcja(czysty(linie.get(od - 1)))) {
            od--;
         }

         while (doo < linie.size() - 1 && opcja(czysty(linie.get(doo + 1)))) {
            doo++;
         }

         if (doo == od) {
            return null;
         }

         int zn = 0;

         for (int i = od; i <= doo; i++) {
            if (znacznik(czysty(linie.get(i)))) {
               zn++;
            }
         }

         if (zn > 0 && zn <= doo - od) {
            return znacznik(czysty(linie.get(n)));
         }

         class_5251 kn = kolor(linie.get(n));
         int takie = 0;
         int inne = 0;
         class_5251 wspolny = null;
         boolean jedenWspolny = true;

         for (int i = od; i <= doo; i++) {
            if (i != n) {
               class_5251 k = kolor(linie.get(i));
               if (Objects.equals(k, kn)) {
                  takie++;
               } else {
                  inne++;
                  if (wspolny == null) {
                     wspolny = k;
                  } else if (!wspolny.equals(k)) {
                     jedenWspolny = false;
                  }
               }
            }
         }

         if (takie == 0 && inne > 0 && jedenWspolny) {
            return true;
         } else {
            return takie > 0 && inne == 1 ? false : null;
         }
      }
   }

   private static boolean opcja(String s) {
      if (znacznik(s)) {
         s = s.substring(1).trim();
      }

      return !s.isEmpty() && s.split("\\s+").length <= 4 && !s.contains("klik") && !s.contains(":");
   }

   private static void zrzut(class_1799 st) {
      try {
         StringBuilder b = new StringBuilder(st.method_7964().getString()).append('\n');
         class_9290 lore = (class_9290)st.method_57824(class_9334.field_49632);
         if (lore != null) {
            for (class_2561 t : lore.comp_2400()) {
               b.append(t.getString()).append("   [").append(kolor(t)).append("]\n");
            }
         }

         b.append("-> ").append(najnowsze(st)).append('\n');
         Path p = FabricLoader.getInstance().getGameDir().resolve("rynekklikacz").resolve("sortowanie.txt");
         Files.createDirectories(p.getParent());
         Files.writeString(p, b.toString(), StandardCharsets.UTF_8);
      } catch (Exception var5) {
      }
   }

   public static void rysuj(class_310 mc, class_437 screen, class_332 ctx) {
      long t = System.currentTimeMillis();
      HandledScreenAccessor a = (HandledScreenAccessor)screen;
      if (t < ostrzezenieDo) {
         class_1735 s = slot(mc);
         if (s == null) {
            return;
         }

         if (!Boolean.FALSE.equals(najnowsze(s.method_7677()))) {
            ostrzezenieDo = 0L;
            okDo = t + 2500L;
            return;
         }

         int sx = a.rynekklikacz$getX() + s.field_7873;
         int sy = a.rynekklikacz$getY() + s.field_7872;
         float puls = (float)(0.5 + 0.5 * Math.sin(t / 130.0));
         int kol = Draw.mix(-865972, -1090725, puls);
         ctx.method_51448().method_22903();
         ctx.method_51448().method_46416(0.0F, 0.0F, 400.0F);
         ctx.method_25294(sx - 2, sy - 2, sx + 18, sy, kol);
         ctx.method_25294(sx - 2, sy + 16, sx + 18, sy + 18, kol);
         ctx.method_25294(sx - 2, sy, sx, sy + 16, kol);
         ctx.method_25294(sx + 16, sy, sx + 18, sy + 16, kol);
         ctx.method_25294(sx, sy, sx + 16, sy + 16, Draw.alpha(kol, 48));
         int bob = (int)Math.round(Math.abs(Math.sin(t / 160.0)) * 6.0);
         int tx = sx + 20 + bob;
         int ty = sy + 8;

         for (int i = 0; i < 8; i++) {
            ctx.method_25294(tx + i - 1, ty - i - 1, tx + i + 2, ty + i + 2, -16777216);
         }

         ctx.method_25294(tx + 7, ty - 4, tx + 21, ty + 5, -16777216);

         for (int i = 0; i < 8; i++) {
            ctx.method_25294(tx + i, ty - i, tx + i + 1, ty + i + 1, kol);
         }

         ctx.method_25294(tx + 8, ty - 3, tx + 20, ty + 4, kol);
         String l1 = "Nie mogę ustawić sortowania na Najnowsze!";
         String l2 = "Ustaw ręcznie na ostatnim slocie";
         String l3 = "Klikacz zatrzymany, potem daj Start";
         int w = Math.max(mc.field_1772.method_1727(l1), Math.max(mc.field_1772.method_1727(l2), mc.field_1772.method_1727(l3))) + 14;
         int h = 42;
         int bx = sx + 50;
         int by = sy + 8 - h / 2;
         if (bx + w > screen.field_22789 - 4) {
            bx = Math.max(4, screen.field_22789 - 4 - w);
            by = sy + 26;
         }

         Draw.shadow(ctx, bx, by, w, h);
         Draw.box(ctx, bx, by, w, h, -15657959, kol);
         ctx.method_25294(bx + 1, by + 1, bx + 3, by + h - 1, kol);
         ctx.method_51433(mc.field_1772, l1, bx + 8, by + 6, -865972, false);
         ctx.method_51433(mc.field_1772, l2, bx + 8, by + 18, -1512205, false);
         ctx.method_51433(mc.field_1772, l3, bx + 8, by + 29, -7629657, false);
         ctx.method_51448().method_22909();
      }
   }
}
