package pl.rynekklikacz;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.fabricmc.loader.api.FabricLoader;

public final class Historia {
   private static final long CO_ILE = 300000L;
   private static final ExecutorService ZAPIS = Executors.newSingleThreadExecutor(r -> {
      Thread t = new Thread(r, "RynekKlikacz-historia");
      t.setDaemon(true);
      return t;
   });
   private static Map<Long, List<Historia.Punkt>> dane;
   private static final Map<Long, Long> ostatnio = new HashMap<>();
   private static StringBuilder bufor = new StringBuilder();
   private static long zapisano;
   private static int wersja;

   private Historia() {
   }

   private static Path plik() {
      return FabricLoader.getInstance().getGameDir().resolve("rynekklikacz").resolve("historia.csv");
   }

   public static long hash(String s) {
      long h = -3750763034362895579L;

      for (int i = 0; i < s.length(); i++) {
         h ^= s.charAt(i);
         h *= 1099511628211L;
      }

      return h;
   }

   public static int wersja() {
      return wersja;
   }

   private static Map<Long, List<Historia.Punkt>> dane() {
      if (dane == null) {
         dane = new HashMap<>();
         Path p = plik();
         if (Files.exists(p)) {
            String l;
            try (BufferedReader r = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
               while ((l = r.readLine()) != null) {
                  String[] c = l.split(";");
                  if (c.length >= 5) {
                     try {
                        Historia.Punkt pk = new Historia.Punkt(
                           Long.parseLong(c[0]), Double.parseDouble(c[2]), Integer.parseInt(c[3]), Long.parseUnsignedLong(c[4], 16)
                        );
                        dane.computeIfAbsent(Long.parseUnsignedLong(c[1], 16), k -> new ArrayList<>()).add(pk);
                        ostatnio.merge(pk.oferta, pk.t, Math::max);
                     } catch (NumberFormatException var6) {
                     }
                  }
               }
            } catch (Exception var8) {
            }
         } else {
            for (Wpis w : Skaner.wszystkie()) {
               if (w.cena >= 0L) {
                  zapiszPunkt(w, w.pierwszy);
                  if (w.ostatni - w.pierwszy >= 300000L) {
                     zapiszPunkt(w, w.ostatni);
                  }
               }
            }
         }

         for (List<Historia.Punkt> l : dane.values()) {
            l.sort(Comparator.comparingLong(Historia.Punkt::t));
         }
      }

      return dane;
   }

   public static void dodaj(Wpis w, long teraz) {
      if (w.cena >= 0L) {
         dane();
         Long ost = ostatnio.get(hash(w.klucz));
         if (ost == null || teraz - ost >= 300000L) {
            zapiszPunkt(w, teraz);
         }
      }
   }

   private static void zapiszPunkt(Wpis w, long t) {
      long g = hash(w.grupa());
      long o = hash(w.klucz);
      Historia.Punkt pk = new Historia.Punkt(t, w.zaSztukeD(), w.ilosc, o);
      List<Historia.Punkt> l = dane.computeIfAbsent(g, k -> new ArrayList<>());
      if (!l.isEmpty() && l.get(l.size() - 1).t > t) {
         int i = l.size();

         while (i > 0 && l.get(i - 1).t > t) {
            i--;
         }

         l.add(i, pk);
      } else {
         l.add(pk);
      }

      ostatnio.merge(o, t, Math::max);
      bufor.append(t)
         .append(';')
         .append(Long.toHexString(g))
         .append(';')
         .append(String.format(Locale.ROOT, "%.4f", pk.cena))
         .append(';')
         .append(w.ilosc)
         .append(';')
         .append(Long.toHexString(o))
         .append('\n');
      wersja++;
   }

   public static List<Historia.Punkt> punkty(String grupa) {
      return dane().getOrDefault(hash(grupa), List.of());
   }

   public static void tick() {
      if (bufor.length() > 0 && System.currentTimeMillis() - zapisano > 3000L) {
         zapisz();
      }
   }

   public static void zapisz() {
      Runnable r = zrzut();
      if (r != null) {
         ZAPIS.execute(r);
      }
   }

   public static void zapiszTeraz() {
      Runnable r = zrzut();
      if (r != null) {
         r.run();
      }
   }

   private static Runnable zrzut() {
      if (bufor.length() == 0) {
         return null;
      }

      String tekst = bufor.toString();
      bufor = new StringBuilder();
      zapisano = System.currentTimeMillis();
      return () -> {
         synchronized (ZAPIS) {
            try {
               Files.createDirectories(plik().getParent());
               Files.writeString(plik(), tekst, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (Exception var4) {
            }
         }
      };
   }

   public record Punkt(long t, double cena, int ilosc, long oferta) {
   }
}
