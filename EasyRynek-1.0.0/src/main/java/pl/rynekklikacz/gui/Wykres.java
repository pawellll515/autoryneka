package pl.rynekklikacz.gui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.class_156;
import net.minecraft.class_327;
import net.minecraft.class_332;
import pl.rynekklikacz.Analiza;
import pl.rynekklikacz.Historia;

final class Wykres {
   static final String[] PRESETY = new String[]{"15m", "30m", "1h", "6h", "24h", "7d", "30d", "Całość"};
   static final long[] DLUGOSCI = new long[]{900000L, 1800000L, 3600000L, 21600000L, 86400000L, 604800000L, 2592000000L, -1L};
   private static final long[] KROKI = new long[]{
      60000L,
      300000L,
      900000L,
      1800000L,
      3600000L,
      10800000L,
      21600000L,
      43200000L,
      86400000L,
      172800000L,
      604800000L,
      1209600000L,
      2592000000L,
      7776000000L,
      31536000000L
   };
   private static final Pattern CZESC = Pattern.compile("(\\d+(?:[.,]\\d+)?)\\s*([a-ząćęłńóśźż]*)");
   private static final SimpleDateFormat GODZ = new SimpleDateFormat("HH:mm");
   private static final SimpleDateFormat DZIEN = new SimpleDateFormat("dd.MM");
   private static final SimpleDateFormat PELNA = new SimpleDateFormat("dd.MM HH:mm:ss");
   private static final int[] POZIOMY = new int[]{-10755185, -865972, -1090725};
   private final BazaScreen ekran;
   final String nazwa;
   Analiza an;
   private final List<Historia.Punkt> punkty;
   private final long[] zyjeOd;
   private final long[] zyjeDo;
   private final double[] zyjeCena;
   private int etykietaX;
   int preset;
   private double od;
   private double doo;
   private double celOd;
   private double celDo;
   private double yMin = -1.0;
   private double yMax = -1.0;
   private float rysuj;
   private boolean ciagnie;
   private double ciagX;
   private double ciagOd;
   private double ciagDo;
   private long ostatniKlik;
   private int px;
   private int py;
   private int pw;
   private int ph;
   private int ox;
   private double yZoom = 1.0;

   Wykres(BazaScreen ekran, String grupa, String nazwa, Analiza an) {
      this.ekran = ekran;
      this.nazwa = nazwa;
      this.an = an;
      this.punkty = new ArrayList<>(Historia.punkty(grupa));
      Map<Long, long[]> zakresy = new LinkedHashMap<>();
      Map<Long, Double> cenyOfert = new HashMap<>();

      for (Historia.Punkt p : this.punkty) {
         long[] z = zakresy.computeIfAbsent(p.oferta(), k -> new long[]{p.t(), p.t()});
         z[0] = Math.min(z[0], p.t());
         z[1] = Math.max(z[1], p.t());
         cenyOfert.put(p.oferta(), p.cena());
      }

      this.zyjeOd = new long[zakresy.size()];
      this.zyjeDo = new long[zakresy.size()];
      this.zyjeCena = new double[zakresy.size()];
      int n = 0;

      for (Entry<Long, long[]> e : zakresy.entrySet()) {
         this.zyjeOd[n] = e.getValue()[0];
         this.zyjeDo[n] = e.getValue()[1] + 300000L;
         this.zyjeCena[n] = cenyOfert.get(e.getKey());
         n++;
      }

      long teraz = System.currentTimeMillis();
      boolean wDobie = !this.punkty.isEmpty()
         && this.punkty.get(this.punkty.size() - 1).t() > teraz - DLUGOSCI[4]
         && this.punkty.get(0).t() < teraz - DLUGOSCI[4];
      this.ustaw(wDobie ? 4 : 7);
      this.od = this.celOd;
      this.doo = this.celDo;
   }

   int ile() {
      return this.punkty.size();
   }

   void ustaw(int p) {
      this.preset = p;
      this.ustawDlugosc(DLUGOSCI[p]);
   }

   void ustawDlugosc(long dl) {
      long teraz = System.currentTimeMillis();
      this.celDo = teraz;
      if (dl < 0L) {
         long pierwszy = this.punkty.isEmpty() ? teraz - 3600000L : this.punkty.get(0).t();
         long margines = Math.max(60000L, (teraz - pierwszy) / 30L);
         this.celOd = pierwszy - margines;
         this.celDo = teraz + margines / 2L;
      } else {
         this.celOd = teraz - dl;
      }

      this.rysuj = 0.0F;
   }

   static long parsuj(String s) {
      String t = s.toLowerCase(Locale.ROOT).trim();
      if (t.isEmpty()) {
         return -1L;
      }

      Matcher m = CZESC.matcher(t);
      double suma = 0.0;

      boolean jest;
      for (jest = false; m.find(); jest = true) {
         double v = Double.parseDouble(m.group(1).replace(',', '.'));
         String j = m.group(2);
         long ms;
         if (!j.isEmpty() && !j.startsWith("h") && !j.startsWith("g")) {
            if (!j.startsWith("mi") || !j.startsWith("mies") && !j.equals("miesiac") && !j.equals("miesiąc")) {
               if (j.equals("msc") || j.equals("mc") || j.equals("mo")) {
                  ms = 2592000000L;
               } else if (j.startsWith("m")) {
                  ms = 60000L;
               } else if (j.startsWith("s")) {
                  ms = 1000L;
               } else if (j.startsWith("d")) {
                  ms = 86400000L;
               } else if (!j.startsWith("t") && !j.startsWith("w")) {
                  if (!j.startsWith("r") && !j.startsWith("y")) {
                     return -1L;
                  }

                  ms = 31536000000L;
               } else {
                  ms = 604800000L;
               }
            } else {
               ms = 2592000000L;
            }
         } else {
            ms = 3600000L;
         }

         suma += v * ms;
      }

      return jest && suma >= 60000.0 ? Math.round(suma) : -1L;
   }

   static String dlugosc(long ms) {
      long m = ms / 60000L;
      if (m < 60L) {
         return m + " min";
      }

      long h = m / 60L;
      if (h < 48L) {
         return h + " h" + (m % 60L > 0L ? " " + m % 60L + " min" : "");
      }

      long d = h / 24L;
      return d + " d" + (h % 24L > 0L ? " " + h % 24L + " h" : "");
   }

   private double tNaX(double t) {
      return this.px + (t - this.od) / (this.doo - this.od) * this.pw;
   }

   private double xNaT(double x) {
      return this.od + (x - this.px) / this.pw * (this.doo - this.od);
   }

   private double cNaY(double c) {
      return this.py + this.ph - (c - this.yMin) / (this.yMax - this.yMin) * this.ph;
   }

   private int ekranY(double y) {
      return (int)Math.round(y);
   }

   void render(class_332 c, int x, int y, int w, int h, int mx, int my) {
      class_327 tr = this.ekran.tr();
      this.od = this.ekran.lerpD(this.od, this.celOd, 14.0F);
      this.doo = this.ekran.lerpD(this.doo, this.celDo, 14.0F);
      this.rysuj = this.ekran.lerpF(this.rysuj, 1.0F, 5.0F);
      this.ox = x;
      this.px = x + 46;
      this.py = y + 6;
      this.pw = w - 54;
      this.ph = h - 20;
      Draw.box(c, x, y, w, h, this.ekran.a(-16250354), this.ekran.a(-14275269));
      List<Historia.Punkt> widoczne = new ArrayList<>();

      for (Historia.Punkt p : this.punkty) {
         if (p.t() >= this.od && p.t() <= this.doo) {
            widoczne.add(p);
         }
      }

      double[] ceny = widoczne.stream().mapToDouble(Historia.Punkt::cena).sorted().toArray();
      double hi;
      double lo;
      if (ceny.length == 0) {
         double r = this.an.rynkowa > 0.0 ? this.an.rynkowa : 1.0;
         lo = r * 0.7;
         hi = r * 1.3;
      } else {
         double p75 = ceny[Math.min(ceny.length - 1, (int)(ceny.length * 0.75))];
         lo = ceny[0];
         hi = p75 * 1.3;
         if (this.an.rynkowa > 0.0) {
            hi = Math.max(hi, this.an.progOver * 1.15);
         }

         if (this.an.jestLow()) {
            lo = Math.min(lo, this.an.progLow);
         }

         hi = Math.min(hi, ceny[ceny.length - 1] * 1.05 + 0.01);
         if (this.an.rynkowa > 0.0) {
            hi = Math.max(hi, this.an.rynkowa * 1.12);
         }

         lo = Math.min(lo, hi * 0.9);
         hi = lo + (hi - lo) * this.yZoom;
         double zapas = (hi - lo) * 0.08 + 0.01;
         lo = Math.max(0.0, lo - zapas);
         hi += zapas;
      }

      if (this.yMin < 0.0) {
         this.yMin = lo;
         this.yMax = hi;
      }

      this.yMin = this.ekran.lerpD(this.yMin, lo, 10.0F);
      this.yMax = this.ekran.lerpD(this.yMax, hi, 10.0F);
      if (this.yMax - this.yMin < 1.0E-6) {
         this.yMax = this.yMin + 1.0;
      }

      for (int i = 0; i <= 4; i++) {
         double v = this.yMin + (this.yMax - this.yMin) * i / 4.0;
         int ly = this.ekranY(this.cNaY(v));
         c.method_25294(this.px, ly, this.px + this.pw, ly + 1, this.ekran.a(318767103));
         String l = BazaScreen.krotkoD(v);
         this.ekran.text(c, l, this.px - 4 - tr.method_1727(l), ly - 4, -10919566);
      }

      double span = this.doo - this.od;
      long krok = KROKI[KROKI.length - 1];

      for (long k : KROKI) {
         if (k / span * this.pw >= 52.0) {
            krok = k;
            break;
         }
      }

      long strefa = TimeZone.getDefault().getOffset((long)this.od);
      long start = ((long)this.od + strefa) / krok * krok - strefa;
      SimpleDateFormat f = krok >= 86400000L ? DZIEN : GODZ;

      for (long t = start; t <= this.doo; t += krok) {
         if (!(t < this.od)) {
            int lx = (int)Math.round(this.tNaX(t));
            c.method_25294(lx, this.py, lx + 1, this.py + this.ph, this.ekran.a(218103807));
            String l = f.format(new Date(t));
            if (krok < 86400000L && (t + strefa) % 86400000L == 0L) {
               l = DZIEN.format(new Date(t));
            }

            int lw = tr.method_1727(l);
            if (lx - lw / 2 > this.px - 10 && lx + lw / 2 < this.px + this.pw + 10) {
               this.ekran.text(c, l, lx - lw / 2, this.py + this.ph + 4, -10919566);
            }
         }
      }

      c.method_44379(this.px, this.py, this.px + this.pw, this.py + this.ph);
      this.progi(c, tr);
      int kubly = Math.max(6, Math.min(90, this.pw / 7));
      double[] rynek = new double[kubly];
      int[] ileWKuble = new int[kubly];
      double[] bufor = new double[this.zyjeCena.length];

      for (int i = 0; i < kubly; i++) {
         double k0 = this.od + span * i / kubly;
         double k1 = this.od + span * (i + 1) / kubly;
         int ile = 0;

         for (int j = 0; j < this.zyjeCena.length; j++) {
            if (this.zyjeOd[j] <= k1 && this.zyjeDo[j] >= k0) {
               bufor[ile++] = this.zyjeCena[j];
            }
         }

         ileWKuble[i] = ile;
         if (ile == 0) {
            rynek[i] = Double.NaN;
         } else {
            double[] t = Arrays.copyOf(bufor, ile);
            Arrays.sort(t);
            rynek[i] = Analiza.podloga(t);
         }
      }

      int[] posort = Arrays.stream(ileWKuble).filter(vx -> vx > 0).sorted().toArray();
      int typowo = posort.length == 0 ? 0 : posort[posort.length / 2];
      int minimum = Math.max(1, Math.min(3, typowo) > 0 ? Math.max(Math.min(3, typowo), (int)(typowo * 0.3)) : 1);

      for (int i = 0; i < kubly; i++) {
         if (ileWKuble[i] < minimum) {
            rynek[i] = Double.NaN;
         }
      }

      int granica = this.px + Math.round(this.pw * this.rysuj);
      int poprzX = -1;
      int poprzI = -10;
      double poprzY = 0.0;

      for (int i = 0; i < kubly; i++) {
         if (!Double.isNaN(rynek[i])) {
            int bx = (int)Math.round(this.px + (i + 0.5) * this.pw / kubly);
            double byv = this.cNaY(Math.min(rynek[i], this.yMax));
            if (poprzX >= 0 && i - poprzI <= 4) {
               this.linia(c, poprzX, poprzY, bx, byv, granica);
            } else {
               if (poprzX >= 0) {
                  this.kropki(c, poprzX, poprzY, bx, byv, granica);
               }

               if (bx <= granica) {
                  c.method_25294(bx - 1, this.ekranY(byv) - 1, bx + 1, this.ekranY(byv) + 1, this.ekran.a(-865972));
               }
            }

            poprzX = bx;
            poprzI = i;
            poprzY = byv;
         }
      }

      Historia.Punkt najblizszy = null;
      double najD = 49.0;
      boolean wWykresie = BazaScreen.in(mx, my, this.px, this.py, this.pw, this.ph);

      for (Historia.Punkt p : widoczne) {
         int qx = (int)Math.round(this.tNaX(p.t()));
         if (qx <= granica) {
            boolean ponad = p.cena() > this.yMax;
            int qy = ponad ? this.py + 2 : this.ekranY(this.cNaY(p.cena()));
            int kol = POZIOMY[this.an.poziom(p.cena())];
            c.method_25294(qx - 1, qy - 1, qx + 1, qy + 1, this.ekran.a(Draw.alpha(kol, 176)));
            if (wWykresie) {
               double d = (qx - mx) * (qx - mx) + (qy - my) * (qy - my);
               if (d < najD) {
                  najD = d;
                  najblizszy = p;
               }
            }
         }
      }

      c.method_44380();
      if (this.ciagnie) {
         double dt = (mx - this.ciagX) / this.pw * (this.ciagDo - this.ciagOd);
         this.celOd = this.ciagOd - dt;
         this.celDo = this.ciagDo - dt;
         this.od = this.celOd;
         this.doo = this.celDo;
         this.preset = -1;
      }

      if (wWykresie && !this.ciagnie) {
         c.method_25294(mx, this.py, mx + 1, this.py + this.ph, this.ekran.a(822083583));
         List<String> linie = new ArrayList<>();
         List<Integer> kolory = new ArrayList<>();
         if (najblizszy != null) {
            int qx = (int)Math.round(this.tNaX(najblizszy.t()));
            int qy = najblizszy.cena() > this.yMax ? this.py + 2 : this.ekranY(this.cNaY(najblizszy.cena()));
            int kol = POZIOMY[this.an.poziom(najblizszy.cena())];
            Draw.rect(c, qx - 3, qy - 3, 7, 7, this.ekran.a(-1512205));
            c.method_25294(qx - 2, qy - 2, qx + 3, qy + 3, this.ekran.a(kol));
            linie.add(BazaScreen.cenaD(najblizszy.cena()) + (najblizszy.ilosc() > 1 ? " / szt." : ""));
            kolory.add(kol);
            if (najblizszy.ilosc() > 1) {
               linie.add(najblizszy.ilosc() + " szt. za " + BazaScreen.cenaD(najblizszy.cena() * najblizszy.ilosc()));
               kolory.add(-7629657);
            }

            linie.add(Analiza.NAZWY[this.an.poziom(najblizszy.cena())] + " price");
            kolory.add(kol);
            linie.add(PELNA.format(new Date(najblizszy.t())));
            kolory.add(-10919566);
         } else {
            double t = this.xNaT(mx);
            int i = (int)Math.max(0.0, Math.min(kubly - 1, (t - this.od) / span * kubly));
            long k0 = (long)(this.od + span * i / kubly);
            long k1 = (long)(this.od + span * (i + 1) / kubly);
            SimpleDateFormat ff = span > 1.728E8 ? PELNA : (span > 7200000.0 ? GODZ : new SimpleDateFormat("HH:mm:ss"));
            linie.add(ff.format(new Date(k0)) + " – " + ff.format(new Date(k1)));
            kolory.add(-7629657);
            if (ileWKuble[i] > 0) {
               linie.add("Cena rynku: " + BazaScreen.cenaD(rynek[i]));
               kolory.add(-865972);
               linie.add(
                  ileWKuble[i]
                     + (
                        ileWKuble[i] == 1
                           ? " oferta"
                           : (ileWKuble[i] % 10 < 2 || ileWKuble[i] % 10 > 4 || ileWKuble[i] % 100 >= 12 && ileWKuble[i] % 100 <= 14 ? " ofert" : " oferty")
                     )
                     + " na rynku"
               );
               kolory.add(-10919566);
            } else {
               linie.add("brak ofert");
               kolory.add(-10919566);
            }

            double cy = this.yMax - (double)(my - this.py) / this.ph * (this.yMax - this.yMin);
            linie.add("kursor: " + BazaScreen.cenaD(cy));
            kolory.add(-10919566);
         }

         this.dymek(c, tr, mx, my, linie, kolory, x + w);
      }

      if (widoczne.isEmpty()) {
         String s = this.punkty.isEmpty() ? "Brak historii tego przedmiotu" : "Brak ofert w tym zakresie";
         this.ekran.text(c, s, this.px + (this.pw - tr.method_1727(s)) / 2, this.py + this.ph / 2 - 4, -10919566);
      }
   }

   private void progi(class_332 c, class_327 tr) {
      if (!(this.an.rynkowa <= 0.0)) {
         this.etykietaX = this.px + this.pw - 2;
         this.przerywana(c, this.an.progOver, POZIOMY[2], "Over", tr);
         this.przerywana(c, this.an.rynkowa, POZIOMY[1], "Mid", tr);
         if (this.an.jestLow()) {
            this.przerywana(c, this.an.progLow, POZIOMY[0], "Low", tr);
         }
      }
   }

   private void przerywana(class_332 c, double v, int kol, String l, class_327 tr) {
      if (!(v < this.yMin) && !(v > this.yMax)) {
         int ly = this.ekranY(this.cNaY(v));

         for (int i = this.px; i < this.px + this.pw; i += 6) {
            c.method_25294(i, ly, Math.min(i + 3, this.px + this.pw), ly + 1, this.ekran.a(Draw.alpha(kol, 144)));
         }

         int lw = tr.method_1727(l) + 4;
         int lx = this.etykietaX - lw;
         c.method_25294(lx, ly - 5, this.etykietaX, ly + 5, this.ekran.a(Draw.alpha(724242, 208)));
         this.ekran.text(c, l, lx + 2, ly - 4, kol);
         this.etykietaX = lx - 3;
      }
   }

   private void linia(class_332 c, int x0, double y0, int x1, double y1, int granica) {
      int dol = this.py + this.ph;

      for (int xx = x0; xx < x1 && xx <= granica; xx++) {
         double t = (double)(xx - x0) / Math.max(1, x1 - x0);
         double yy = y0 + (y1 - y0) * t;
         double yn = y0 + (y1 - y0) * Math.min(1.0, (double)(xx + 1 - x0) / Math.max(1, x1 - x0));
         int a = this.ekranY(Math.min(yy, yn));
         int b = this.ekranY(Math.max(yy, yn));
         c.method_25296(xx, b + 1, xx + 1, dol, this.ekran.a(Draw.alpha(-865972, 56)), this.ekran.a(Draw.alpha(-865972, 2)));
         c.method_25294(xx, a - 1, xx + 1, b + 1, this.ekran.a(-865972));
      }
   }

   private void kropki(class_332 c, int x0, double y0, int x1, double y1, int granica) {
      for (int xx = x0; xx < x1 && xx <= granica; xx += 4) {
         double t = (double)(xx - x0) / Math.max(1, x1 - x0);
         int yy = this.ekranY(y0 + (y1 - y0) * t);
         c.method_25294(xx, yy, xx + 2, yy + 1, this.ekran.a(Draw.alpha(-865972, 112)));
      }
   }

   private void dymek(class_332 c, class_327 tr, int mx, int my, List<String> linie, List<Integer> kolory, int prawy) {
      int w = 0;

      for (String l : linie) {
         w = Math.max(w, tr.method_1727(l));
      }

      w += 10;
      int h = linie.size() * 10 + 6;
      int bx = mx + 10;
      if (bx + w > prawy - 2) {
         bx = mx - 10 - w;
      }

      int by = Math.max(this.py, Math.min(my - h / 2, this.py + this.ph - h));
      c.method_51448().method_22903();
      c.method_51448().method_46416(0.0F, 0.0F, 400.0F);
      Draw.box(c, bx, by, w, h, this.ekran.a(-267316199), this.ekran.a(-12892839));

      for (int i = 0; i < linie.size(); i++) {
         this.ekran.text(c, linie.get(i), bx + 5, by + 4 + i * 10, kolory.get(i));
      }

      c.method_51448().method_22909();
   }

   String opisZakresu() {
      SimpleDateFormat f = this.doo - this.od > 8.64E7 ? DZIEN : GODZ;
      SimpleDateFormat g = this.doo - this.od > 8.64E7 ? new SimpleDateFormat("dd.MM HH:mm") : GODZ;
      return dlugosc((long)(this.celDo - this.celOd))
         + " · "
         + g.format(new Date((long)this.celOd))
         + " – "
         + g.format(new Date((long)this.celDo))
         + (f == DZIEN ? "" : "");
   }

   boolean klik(double mx, double my) {
      if (!BazaScreen.in(mx, my, this.px, this.py, this.pw, this.ph)) {
         return false;
      } else {
         long teraz = class_156.method_658();
         if (teraz - this.ostatniKlik < 300L) {
            this.ustaw(this.preset >= 0 ? this.preset : 7);
            this.yZoom = 1.0;
            this.ostatniKlik = 0L;
            return true;
         } else {
            this.ostatniKlik = teraz;
            this.ciagnie = true;
            this.ciagX = mx;
            this.ciagOd = this.celOd;
            this.ciagDo = this.celDo;
            return true;
         }
      }
   }

   void puszczony() {
      this.ciagnie = false;
   }

   boolean kolko(double mx, double my, double v) {
      if (BazaScreen.in(mx, my, this.ox, this.py, this.px - this.ox, this.ph)) {
         this.yZoom = Math.max(0.03, Math.min(1.0, this.yZoom * (v > 0.0 ? 0.8 : 1.25)));
         return true;
      }

      if (!BazaScreen.in(mx, my, this.px, this.py, this.pw, this.ph)) {
         return false;
      }

      double f = v > 0.0 ? 0.8 : 1.25;
      double kotwica = this.celOd + (mx - this.px) / this.pw * (this.celDo - this.celOd);
      double nOd = kotwica - (kotwica - this.celOd) * f;
      double nDo = kotwica + (this.celDo - kotwica) * f;
      if (nDo - nOd < 60000.0) {
         return true;
      }

      if (nDo - nOd > 1.5768E11) {
         return true;
      }

      this.celOd = nOd;
      this.celDo = nDo;
      this.preset = -1;
      return true;
   }

   static double[] posortuj(List<Double> l) {
      double[] t = l.stream().mapToDouble(Double::doubleValue).toArray();
      Arrays.sort(t);
      return t;
   }
}
