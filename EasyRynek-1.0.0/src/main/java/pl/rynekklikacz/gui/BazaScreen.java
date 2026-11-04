package pl.rynekklikacz.gui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.class_124;
import net.minecraft.class_156;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2477;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_437;
import net.minecraft.class_5348;
import net.minecraft.class_5481;
import pl.rynekklikacz.Analiza;
import pl.rynekklikacz.Klikacz;
import pl.rynekklikacz.RynekKlikaczClient;
import pl.rynekklikacz.Skaner;
import pl.rynekklikacz.Snajper;
import pl.rynekklikacz.Ustawienia;
import pl.rynekklikacz.Wpis;
import pl.rynekklikacz.Zakupy;

public class BazaScreen extends class_437 {
   private static final int HEADER = 36;
   private static final int ROW = 24;
   private static final int FOOT = 22;
   private static final int PASEK = 22;
   private static final int ENCHANT = -4875009;
   private static final int TANIO = -10755185;
   private static final int DROGO = -1082517;
   private static final int[] POZIOMY = new int[]{-10755185, -865972, -1090725};
   private static final String[] SORTY = new String[]{"Najnowsze", "Nazwa", "Cena", "Okazje"};
   private static final class_1799 LOGO = new class_1799(class_1802.field_27070);
   private static final SimpleDateFormat DATA = new SimpleDateFormat("dd.MM HH:mm");
   private static int sort;
   private static String szukane = "";
   private final List<BazaScreen.Hot> hots = new ArrayList<>();
   private final Map<Object, Float> hover = new HashMap<>();
   private class_342 szukaj;
   private class_342 zakres;
   private class_342 profitPole;
   private boolean zostawFokus;
   private Wykres wykres;
   private BazaScreen.Grupa wykresGrupa;
   private long zakresBlad;
   private List<BazaScreen.Grupa> grupy = new ArrayList<>();
   private int ofert;
   private int listaWersja = -1;
   private String listaSzukane;
   private int listaSort = -1;
   private int listaProfit = -1;
   private BazaScreen.Grupa otwarta;
   private String otwartaKlucz;
   private BazaScreen.Grupa wybranaGrupa;
   private Wpis wybranaOferta;
   private int filtr = -1;
   private List<Wpis> widoczne = new ArrayList<>();
   private List<class_5481> opis = List.of();
   private Wpis opisDla;
   private int opisSzer;
   private float scroll;
   private float scrollCel;
   private float scrollGrup;
   private float detScroll;
   private float detScrollCel;
   private float tScroll;
   private float tScrollCel;
   private static int zakladka;
   private float zakladkaX = -1.0F;
   private float zakladkaW;
   private Map<String, BazaScreen.Grupa> wszystkieGrupy = new LinkedHashMap<>();
   private int listaCele = -1;
   private long wyczyscZakupyDo;
   private int profitX;
   private int profitW;
   private float pasek = -1.0F;
   private float chipX = -1.0F;
   private float chipW;
   private float fokus;
   private float czysc;
   private float przejscie = 1.0F;
   private int kierunek = 1;
   private long otwarto;
   private long zamykanie = -1L;
   private long klatka;
   private long wyczyscDo;
   private long ostatniKlik;
   private Object ostatnioKlikniety;
   private float e;
   private float warstwa = 1.0F;
   private float dt;
   private class_1799 tooltip;
   private int px;
   private int py;
   private int pw;
   private int ph;
   private int x;
   private int y;
   private int ty;
   private int sx;
   private int sw;
   private int by;
   private int bb;
   private int lx;
   private int lw;
   private int dx;
   private int dw;

   public BazaScreen() {
      super(class_2561.method_43470("Skaner rynku"));
   }

   protected void method_25426() {
      this.pw = Math.min(this.field_22789 - 24, 500);
      this.ph = Math.min(this.field_22790 - 24, 310);
      this.px = (this.field_22789 - this.pw) / 2;
      this.py = (this.field_22790 - this.ph) / 2;
      if (this.otwarto == 0L) {
         this.otwarto = class_156.method_658();
      }

      this.szukaj = new class_342(this.field_22793, 0, 0, 100, 10, class_2561.method_43470("Szukaj"));
      this.szukaj.method_1858(false);
      this.szukaj.method_1880(64);
      this.szukaj.method_1868(-1512205);
      this.szukaj.method_47404(class_2561.method_43470("Szukaj: nazwa, enchant, lore, id…").method_54663(-10919566));
      this.szukaj.method_1852(szukane);
      this.szukaj.method_1863(v -> {
         szukane = v;
         this.scrollCel = 0.0F;
      });
      this.method_37063(this.szukaj);
      this.zakres = new class_342(this.field_22793, 0, 0, 60, 10, class_2561.method_43470("Zakres"));
      this.zakres.method_1858(false);
      this.zakres.method_1880(24);
      this.zakres.method_1868(-1512205);
      this.zakres.method_47404(class_2561.method_43470("np. 26d").method_54663(-10919566));
      this.method_37063(this.zakres);
      this.profitPole = new class_342(this.field_22793, 0, 0, 20, 10, class_2561.method_43470("Profit"));
      this.profitPole.method_1858(false);
      this.profitPole.method_1880(3);
      this.profitPole.method_1868(POZIOMY[0]);
      this.profitPole.method_1890(v -> v.matches("[0-9]{0,3}"));
      this.profitPole.method_1852(String.valueOf(Ustawienia.profit()));
      this.profitPole.method_1863(v -> {
         try {
            int n = Integer.parseInt(v);
            if (n >= 1 && n <= 200) {
               Ustawienia.profit(n);
            }
         } catch (NumberFormatException var2) {
         }
      });
      this.method_37063(this.profitPole);
   }

   public boolean method_25421() {
      return false;
   }

   private static float ease(float t) {
      float u = 1.0F - t;
      return 1.0F - u * u * u;
   }

   private static float clamp(float v) {
      return Math.max(0.0F, Math.min(1.0F, v));
   }

   class_327 tr() {
      return this.field_22793;
   }

   float lerpF(float a, float b, float speed) {
      return this.lerp(a, b, speed);
   }

   double lerpD(double a, double b, float speed) {
      double k = 1.0 - Math.exp(-this.dt * speed);
      double v = a + (b - a) * k;
      return Math.abs(b - v) <= Math.abs(b) * 1.0E-5 + 1.0E-9 ? b : v;
   }

   private float lerp(float a, float b, float speed) {
      float k = 1.0F - (float)Math.exp(-this.dt * speed);
      float v = a + (b - a) * k;
      return Math.abs(b - v) < 0.01F ? b : v;
   }

   int a(int color) {
      return (int)((color >>> 24) * this.e * this.warstwa) << 24 | color & 16777215;
   }

   static boolean in(double mx, double my, int x, int y, int w, int h) {
      return mx >= x && mx < x + w && my >= y && my < y + h;
   }

   void text(class_332 c, String s, int tx, int tyy, int color) {
      if (this.e * this.warstwa > 0.1F) {
         c.method_51433(this.field_22793, s, tx, tyy, this.a(color), false);
      }
   }

   void text(class_332 c, class_5481 s, int tx, int tyy, int color) {
      if (this.e * this.warstwa > 0.1F) {
         c.method_51430(this.field_22793, s, tx, tyy, this.a(color), false);
      }
   }

   private class_5481 przytnij(class_2561 t, int w) {
      return this.field_22793.method_27525(t) <= w
         ? t.method_30937()
         : class_2477.method_10517()
            .method_30934(
               class_5348.method_29433(
                  new class_5348[]{this.field_22793.method_1714(t, Math.max(0, w - this.field_22793.method_1727("…"))), class_2561.method_43470("…")}
               )
            );
   }

   public void method_25394(class_332 c, int mx, int my, float delta) {
      long teraz = class_156.method_658();
      this.dt = this.klatka == 0L ? 0.0F : Math.min(0.1F, (float)(teraz - this.klatka) / 1000.0F);
      this.klatka = teraz;
      float t = clamp((float)(teraz - this.otwarto) / 260.0F);
      if (this.zamykanie >= 0L) {
         t = Math.min(t, 1.0F - clamp((float)(teraz - this.zamykanie) / 170.0F));
         if (t <= 0.0F) {
            super.method_25419();
            return;
         }
      }

      this.e = ease(t);
      this.przejscie = this.lerp(this.przejscie, 1.0F, 12.0F);
      this.odswiez();
      this.uklad();
      this.szukaj.field_22764 = this.e > 0.5F;
      this.zakres.field_22764 = zakladka == 0 && this.wykres != null && this.e > 0.5F;
      this.profitPole.field_22764 = this.e > 0.5F;
      if (!this.profitPole.method_25370() && !this.profitPole.method_1882().equals(String.valueOf(Ustawienia.profit()))) {
         this.profitPole.method_1852(String.valueOf(Ustawienia.profit()));
      }

      this.tooltip = null;
      super.method_25394(c, mx, my, delta);
      if (this.tooltip != null && this.e > 0.95F) {
         c.method_51446(this.field_22793, this.tooltip, mx, my);
      }
   }

   public void method_25420(class_332 c, int mx, int my, float delta) {
      c.method_25296(0, 0, this.field_22789, this.field_22790, this.a(-1610612736), this.a(-939260664));
      this.hots.clear();
      this.panel(c, mx, my);
   }

   private void uklad() {
      int dy = Math.round((1.0F - this.e) * 18.0F);
      this.x = this.px;
      this.y = this.py + dy;
      this.ty = this.y + 36 + 8;
      int chips = 0;

      for (String s : SORTY) {
         chips += this.field_22793.method_1727(s) + 14 + 3;
      }

      this.sx = this.x + 8;
      this.sw = this.pw - 16 - chips - 4;
      this.by = this.ty + 28;
      this.bb = this.y + this.ph - 22 - 4;
      this.dw = this.pw >= 440 ? 176 : 146;
      this.lx = this.x + 8;
      this.lw = this.pw - 16 - this.dw - 6;
      this.dx = this.lx + this.lw + 6;
      this.szukaj.method_46421(this.sx + 20);
      this.szukaj.method_46419(this.ty + 6);
      this.szukaj.method_25358(this.sw - 36);
   }

   private void odswiez() {
      int profit = Ustawienia.profit();
      if (Skaner.wersja() != this.listaWersja
         || !szukane.equals(this.listaSzukane)
         || sort != this.listaSort
         || profit != this.listaProfit
         || this.listaCele != Snajper.celeWersja()) {
         this.listaCele = Snajper.celeWersja();
         this.listaProfit = profit;
         this.listaWersja = Skaner.wersja();
         this.listaSzukane = szukane;
         this.listaSort = sort;
         String[] slowa = szukane.toLowerCase(Locale.ROOT).trim().split("\\s+");
         Map<String, BazaScreen.Grupa> wszystkie = new LinkedHashMap<>();
         Set<String> pasujace = new HashSet<>();
         this.ofert = 0;

         for (Wpis w : Skaner.wszystkie()) {
            String k = w.grupa();
            BazaScreen.Grupa g = wszystkie.computeIfAbsent(k, kk -> {
               BazaScreen.Grupa n = new BazaScreen.Grupa();
               n.klucz = kk;
               return n;
            });
            g.dodaj(w);
            boolean ok = true;

            for (String s : slowa) {
               if (!s.isEmpty() && !w.tekst().contains(s)) {
                  ok = false;
                  break;
               }
            }

            if (ok) {
               this.ofert++;
               pasujace.add(k);
            }
         }

         this.wszystkieGrupy = wszystkie;
         Map<String, BazaScreen.Grupa> mapa = new LinkedHashMap<>();

         for (Entry<String, BazaScreen.Grupa> e : wszystkie.entrySet()) {
            if (pasujace.contains(e.getKey())) {
               mapa.put(e.getKey(), e.getValue());
            }
         }

         for (BazaScreen.Grupa g : wszystkie.values()) {
            g.ceny.sort(null);
            g.a = new Analiza(g.oferty, profit);
            g.oferty.sort(Comparator.<Wpis>comparingDouble(w -> w.cena < 0L ? Double.MAX_VALUE : w.zaSztukeD()).thenComparingLong(w -> -w.ostatni));
         }

         List<BazaScreen.Grupa> wynik = new ArrayList<>(mapa.values());

         wynik.sort(
            switch (sort) {
               case 1 -> Comparator.comparing(gx -> gx.wzor.nazwa.toLowerCase(Locale.ROOT));
               case 2 -> Comparator.comparingDouble(gx -> gx.rynkowa() < 0.0 ? Double.MAX_VALUE : gx.rynkowa());
               case 3 -> Comparator.<BazaScreen.Grupa>comparingDouble(gx -> gx.a.najlepszyZysk > 0.0 ? -gx.a.najlepszyZysk / gx.a.rynkowa : 1.0)
                  .thenComparingLong(gx -> -gx.ostatni);
               default -> Comparator.comparingLong(gx -> -gx.ostatni);
            }
         );
         this.grupy = wynik;
         if (this.otwarta != null) {
            this.otwarta = mapa.get(this.otwartaKlucz);
            if (this.otwarta == null) {
               this.wyjdz();
            } else {
               this.przefiltruj();
            }
         }

         if (this.wykresGrupa != null) {
            BazaScreen.Grupa nowa = mapa.get(this.wykresGrupa.klucz);
            if (nowa != null) {
               this.wykresGrupa = nowa;
               this.wykres.an = nowa.a;
            }
         }

         BazaScreen.Grupa stara = this.wybranaGrupa;
         this.wybranaGrupa = stara == null ? null : mapa.get(stara.klucz);
         if (this.wybranaGrupa == null) {
            this.wybranaGrupa = this.grupy.isEmpty() ? null : this.grupy.get(0);
         }
      }
   }

   private void otworzWykres(BazaScreen.Grupa g) {
      this.wykres = new Wykres(this, g.klucz, g.wzor.nazwa, g.a);
      this.wykresGrupa = g;
      this.zakres.method_1852("");
      this.przejscie = 0.0F;
      this.kierunek = 1;
      this.method_25395(null);
   }

   private void zamknijWykres() {
      this.wykres = null;
      this.wykresGrupa = null;
      this.przejscie = 0.0F;
      this.kierunek = -1;
      this.method_25395(null);
   }

   private void zastosujZakres() {
      long dl = Wykres.parsuj(this.zakres.method_1882());
      if (dl < 0L) {
         this.zakresBlad = class_156.method_658() + 900L;
      } else {
         this.wykres.preset = -1;
         this.wykres.ustawDlugosc(dl);
         this.method_25395(null);
      }
   }

   private void wykresPanel(class_332 c, int mx, int my) {
      class_327 tr = this.field_22793;
      int wx = this.lx;
      int ww = this.dx + this.dw - this.lx;
      Draw.box(c, wx, this.by, ww, this.bb - this.by, this.a(-15986924), this.a(-14275269));
      int hy = this.by + 1;
      c.method_25294(wx + 1, hy, wx + ww - 1, hy + 22 - 1, this.a(-15591904));
      c.method_25294(wx + 1, hy + 22 - 1, wx + ww - 1, hy + 22, this.a(-14275269));
      String w = "‹ Wróć";
      int bw = tr.method_1727(w) + 12;
      boolean h = in(mx, my, wx + 4, hy + 3, bw, 15);
      Draw.box(c, wx + 4, hy + 3, bw, 15, this.a(h ? Draw.mix(-15986924, -13643391, 0.2F) : -15986924), this.a(h ? -13643391 : -14275269));
      this.text(c, w, wx + 10, hy + 7, h ? -13643391 : -7629657);
      this.hots.add(new BazaScreen.Hot(wx + 4, hy + 3, bw, 15, this::zamknijWykres));
      int cx = wx + bw + 10;

      for (int i = 0; i < Wykres.PRESETY.length; i++) {
         String l = Wykres.PRESETY[i];
         int cw = tr.method_1727(l) + 8;
         boolean wl = this.wykres.preset == i;
         boolean ch = in(mx, my, cx, hy + 3, cw, 15);
         Draw.box(
            c, cx, hy + 3, cw, 15, this.a(wl ? Draw.mix(-15986924, -865972, 0.3F) : (ch ? -15196630 : -15986924)), this.a(!wl && !ch ? -14275269 : -865972)
         );
         this.text(c, l, cx + 4, hy + 7, wl ? Draw.lighten(-865972) : -7629657);
         int nr = i;
         this.hots.add(new BazaScreen.Hot(cx, hy + 3, cw, 15, () -> {
            this.wykres.ustaw(nr);
            this.zakres.method_1852("");
            this.method_25395(null);
         }));
         cx += cw + 2;
      }

      int fw = 76;
      int fx = wx + ww - 6 - fw;
      boolean blad = class_156.method_658() < this.zakresBlad;
      int obrys = blad ? -1090725 : (this.zakres.method_25370() ? -865972 : (in(mx, my, fx, hy + 3, fw, 15) ? -12892839 : -14275269));
      Draw.box(c, fx, hy + 3, fw, 15, this.a(-16250354), this.a(obrys));
      this.hots.add(new BazaScreen.Hot(fx, hy + 3, fw, 15, () -> {
         this.method_25395(this.zakres);
         this.zostawFokus = true;
      }));
      this.zakres.method_46421(fx + 4);
      this.zakres.method_46419(hy + 7);
      this.zakres.method_25358(fw - 8);
      if (cx + 4 < fx) {
         String opis = "Zakres:";
         if (cx + tr.method_1727(opis) + 8 < fx) {
            this.text(c, opis, fx - 4 - tr.method_1727(opis), hy + 7, -10919566);
         }
      }

      this.warstwa = this.przejscie;
      int iy = this.by + 22 + 4;
      String zakresTxt = this.wykres.opisZakresu();
      int zw = tr.method_1727(zakresTxt);
      this.text(c, this.przytnij(this.wykresGrupa.wzor.stack().method_63015(), ww - zw - 24), wx + 8, iy, -1512205);
      this.text(c, zakresTxt, wx + ww - 8 - zw, iy, -7629657);
      int gy = iy + 12;
      this.wykres.render(c, wx + 6, gy, ww - 12, this.bb - gy - 6, mx, my);
      this.warstwa = 1.0F;
   }

   private int listaTabeli(int ileWierszy, int top) {
      int lh = this.bb - top;
      int tresc = ileWierszy * 24 + 4;
      float max = Math.max(0, tresc - lh);
      this.tScrollCel = Math.max(0.0F, Math.min(max, this.tScrollCel));
      this.tScroll = this.lerp(this.tScroll, this.tScrollCel, 16.0F);
      return Math.round(this.tScroll);
   }

   private void suwak(class_332 c, int wx, int ww, int top, int ileWierszy, boolean wLiscie) {
      int lh = this.bb - top;
      int tresc = ileWierszy * 24 + 4;
      float max = Math.max(0, tresc - lh);
      if (!(max <= 0.0F)) {
         int th = Math.max(18, lh * lh / tresc);
         int tyy = top + Math.round((lh - th) * (this.tScroll / max));
         c.method_25294(wx + ww - 4, top + 2, wx + ww - 2, this.bb - 2, this.a(352321535));
         c.method_25294(wx + ww - 4, tyy + 2, wx + ww - 2, tyy + th - 2, this.a(wLiscie ? -7629657 : -12892839));
      }
   }

   private void snajperPanel(class_332 c, int mx, int my) {
      class_327 tr = this.field_22793;
      int wx = this.lx;
      int ww = this.dx + this.dw - this.lx;
      this.warstwa = this.przejscie;
      Draw.box(c, wx, this.by, ww, this.bb - this.by, this.a(-15986924), this.a(-14275269));
      int hy = this.by + 1;
      c.method_25294(wx + 1, hy, wx + ww - 1, hy + 22 - 1, this.a(-15591904));
      c.method_25294(wx + 1, hy + 22 - 1, wx + ww - 1, hy + 22, this.a(-14275269));
      String st = Snajper.zajety() ? Snajper.status : (Klikacz.dziala() ? "Poluje na rynku · kupiono " + Snajper.kupione() : "Czeka na Start na rynku");
      int stc = Snajper.zajety() ? -865972 : (Klikacz.dziala() ? POZIOMY[0] : -10919566);
      float puls = (float)(Math.sin(class_156.method_658() / 200.0) * 0.5 + 0.5);
      this.text(c, "●", wx + 8, hy + 7, Klikacz.dziala() ? Draw.mix(-15986924, stc, 0.4F + 0.6F * puls) : -10919566);
      this.text(c, Draw.trim(tr, st, ww / 2), wx + 18, hy + 7, stc);
      boolean auto = Ustawienia.autoWystaw();
      String at = auto ? "Auto /wystaw: ON" : "Auto /wystaw: OFF";
      int atw = tr.method_1727(at) + 12;
      int atx = wx + ww - 6 - atw;
      boolean ha = in(mx, my, atx, hy + 3, atw, 15);
      int ak = auto ? POZIOMY[0] : -10919566;
      Draw.box(c, atx, hy + 3, atw, 15, this.a(Draw.mix(-15986924, ak, auto ? 0.22F : (ha ? 0.12F : 0.0F))), this.a(!ha && !auto ? -14275269 : ak));
      this.text(c, at, atx + 6, hy + 7, auto ? Draw.lighten(ak) : (ha ? -1512205 : -10919566));
      this.hots.add(new BazaScreen.Hot(atx, hy + 3, atw, 15, () -> Ustawienia.autoWystaw(!Ustawienia.autoWystaw())));
      boolean pow = Ustawienia.autoPowrot();
      String ptx = pow ? "Powrót /ah: ON" : "Powrót /ah: OFF";
      int ptw = tr.method_1727(ptx) + 12;
      int ptxx = atx - 4 - ptw;
      boolean hpw = in(mx, my, ptxx, hy + 3, ptw, 15);
      int pk = pow ? -865972 : -10919566;
      Draw.box(c, ptxx, hy + 3, ptw, 15, this.a(Draw.mix(-15986924, pk, pow ? 0.22F : (hpw ? 0.12F : 0.0F))), this.a(!hpw && !pow ? -14275269 : pk));
      this.text(c, ptx, ptxx + 6, hy + 7, pow ? Draw.lighten(pk) : (hpw ? -1512205 : -10919566));
      this.hots.add(new BazaScreen.Hot(ptxx, hy + 3, ptw, 15, () -> Ustawienia.autoPowrot(!Ustawienia.autoPowrot())));
      List<Object> wiersze = new ArrayList<>();
      List<BazaScreen.Grupa> chciane = new ArrayList<>();

      for (String k : Snajper.cele()) {
         BazaScreen.Grupa g = this.wszystkieGrupy.get(k);
         if (g != null) {
            chciane.add(g);
         }
      }

      wiersze.add("Kupowane (" + Snajper.cele().size() + ")");
      wiersze.addAll(chciane);
      if (!szukane.isBlank()) {
         wiersze.add("Wyniki dla „" + szukane.trim() + "”");

         for (BazaScreen.Grupa g : this.grupy) {
            if (!Snajper.chce(g.klucz)) {
               wiersze.add(g);
            }
         }
      }

      int top = this.by + 22;
      int off = this.listaTabeli(wiersze.size(), top);
      boolean wLiscie = in(mx, my, wx, top, ww, this.bb - top);
      c.method_44379(wx + 1, top + 1, wx + ww - 1, this.bb - 1);

      for (int i = 0; i < wiersze.size(); i++) {
         int ry = top + 2 + i * 24 - off;
         if (ry + 24 >= top && ry <= this.bb) {
            Object o = wiersze.get(i);
            if (o instanceof String naglowek) {
               this.text(c, naglowek, wx + 8, ry + 12, -7629657);
               c.method_25294(wx + 8 + tr.method_1727(naglowek) + 6, ry + 16, wx + ww - 8, ry + 17, this.a(-14275269));
            } else {
               BazaScreen.Grupa g = (BazaScreen.Grupa)o;
               boolean chce = Snajper.chce(g.klucz);
               boolean h = wLiscie && in(mx, my, wx + 1, ry, ww - 2, 24);
               float hv = this.lerp(this.hover.getOrDefault(o, 0.0F), h ? 1.0F : 0.0F, 18.0F);
               if (hv > 0.0F) {
                  this.hover.put(o, hv);
               } else {
                  this.hover.remove(o);
               }

               c.method_25294(wx + 2, ry, wx + ww - 2, ry + 24 - 1, this.a(Draw.mix(chce ? Draw.mix(-15986924, -865972, 0.06F) : -15986924, -15196630, hv)));
               if (chce) {
                  c.method_25294(wx + 2, ry + 3, wx + 4, ry + 24 - 4, this.a(-865972));
               }

               int ix = wx + 8;
               Draw.box(c, ix, ry + 2, 20, 20, this.a(-16250354), this.a(chce ? Draw.mix(-14275269, -865972, 0.6F) : -14275269));
               class_1799 st2 = g.wzor.stack();
               if (this.e > 0.4F && this.przejscie > 0.3F) {
                  c.method_51427(st2, ix + 2, ry + 4);
                  c.method_51432(tr, st2, ix + 2, ry + 4, "");
               }

               if (wLiscie && in(mx, my, ix, ry + 2, 20, 20)) {
                  this.tooltip = st2;
               }

               Analiza an = g.a;
               String bt = chce ? "✓ Kupuję" : "+ Dodaj";
               int bw = tr.method_1727(bt) + 14;
               int bx = wx + ww - 10 - bw;
               boolean hb = in(mx, my, bx, ry + 4, bw, 16);
               if (chce) {
                  Draw.box(c, bx, ry + 4, bw, 16, this.a(hb ? Draw.mix(-865972, -1090725, 0.5F) : -865972), this.a(hb ? -1090725 : -865972));
               } else {
                  Draw.box(c, bx, ry + 4, bw, 16, this.a(hb ? Draw.mix(-15986924, -13643391, 0.25F) : -15986924), this.a(hb ? -13643391 : -14275269));
               }

               String btw = chce && hb ? "✕ Usuń" : bt;
               this.text(c, btw, bx + (bw - tr.method_1727(btw)) / 2, ry + 8, chce ? -16379124 : (hb ? -13643391 : -7629657));
               this.hots.add(new BazaScreen.Hot(bx, Math.max(top, ry + 4), bw, Math.min(16, Math.max(0, this.bb - ry - 4)), () -> Snajper.przelacz(g.klucz)));
               String prog = an.jestLow() ? "kupuj ≤ " + cenaD(an.progLow) : "brak ceny";
               String rynek = an.rynkowa > 0.0 ? "rynek " + cenaD(an.rynkowa) : "";
               int pw1 = Math.max(tr.method_1727(prog), tr.method_1727(rynek));
               int prx = bx - 10 - pw1;
               this.text(c, prog, bx - 10 - tr.method_1727(prog), ry + 4, an.jestLow() ? POZIOMY[0] : -10919566);
               this.text(c, rynek, bx - 10 - tr.method_1727(rynek), ry + 14, -10919566);
               int nx = ix + 26;
               int nw = prx - 8 - nx;
               this.text(c, this.przytnij(g.wzor.stack().method_63015(), nw), nx, ry + 4, -1512205);
               String drugi = g.wzor.kolorowa()
                  ? "każde enchanty · " + g.oferty.size() + " ofert"
                  : (!g.wzor.enchanty.isEmpty() ? String.join(", ", g.wzor.enchanty) : g.oferty.size() + " ofert");
               this.text(c, Draw.trim(tr, drugi, nw), nx, ry + 14, g.wzor.kolorowa() ? -10919566 : -4875009);
            }
         }
      }

      if (chciane.isEmpty() && szukane.isBlank()) {
         String a1 = "Nic jeszcze nie kupujesz";
         String a2 = "Wpisz nazwę w wyszukiwarce i kliknij „+ Dodaj”";
         this.text(c, a1, wx + (ww - tr.method_1727(a1)) / 2, top + (this.bb - top) / 2 - 6, -7629657);
         this.text(c, a2, wx + (ww - tr.method_1727(a2)) / 2, top + (this.bb - top) / 2 + 6, -10919566);
      }

      c.method_44380();
      this.suwak(c, wx, ww, top, wiersze.size(), wLiscie);
      this.warstwa = 1.0F;
   }

   private void zakupyPanel(class_332 c, int mx, int my) {
      class_327 tr = this.field_22793;
      int wx = this.lx;
      int ww = this.dx + this.dw - this.lx;
      this.warstwa = this.przejscie;
      Draw.box(c, wx, this.by, ww, this.bb - this.by, this.a(-15986924), this.a(-14275269));
      List<Zakupy.Zakup> lista = new ArrayList<>();
      String q = szukane.toLowerCase(Locale.ROOT).trim();
      long wydano = 0L;
      double zysk = 0.0;

      for (Zakupy.Zakup z : Zakupy.wszystkie()) {
         if (q.isEmpty() || z.nazwa.toLowerCase(Locale.ROOT).contains(q)) {
            lista.add(z);
            wydano += z.cena;
            zysk += z.zysk;
         }
      }

      int hy = this.by + 1;
      c.method_25294(wx + 1, hy, wx + ww - 1, hy + 22 - 1, this.a(-15591904));
      c.method_25294(wx + 1, hy + 22 - 1, wx + ww - 1, hy + 22, this.a(-14275269));
      int sx2 = wx + 8;
      String s1 = "Kupiono: " + lista.size();
      this.text(c, s1, sx2, hy + 7, -1512205);
      sx2 += tr.method_1727(s1) + 14;
      String s2 = "Wydano: " + cenaD(wydano);
      this.text(c, s2, sx2, hy + 7, -865972);
      sx2 += tr.method_1727(s2) + 14;
      String s3 = "Szacowany zysk: +" + cenaD(zysk);
      this.text(c, s3, sx2, hy + 7, POZIOMY[0]);
      int top = this.by + 22;
      int off = this.listaTabeli(lista.size(), top);
      boolean wLiscie = in(mx, my, wx, top, ww, this.bb - top);
      c.method_44379(wx + 1, top + 1, wx + ww - 1, this.bb - 1);

      for (int i = 0; i < lista.size(); i++) {
         int ry = top + 2 + i * 24 - off;
         if (ry + 24 >= top && ry <= this.bb) {
            Zakupy.Zakup z = lista.get(i);
            boolean h = wLiscie && in(mx, my, wx + 1, ry, ww - 2, 24);
            float hv = this.lerp(this.hover.getOrDefault(z, 0.0F), h ? 1.0F : 0.0F, 18.0F);
            if (hv > 0.0F) {
               this.hover.put(z, hv);
            } else {
               this.hover.remove(z);
            }

            c.method_25294(wx + 2, ry, wx + ww - 2, ry + 24 - 1, this.a(Draw.mix(-15986924, -15196630, hv)));
            c.method_25294(wx + 2, ry + 3, wx + 4, ry + 24 - 4, this.a(POZIOMY[0]));
            int ix = wx + 8;
            Draw.box(c, ix, ry + 2, 20, 20, this.a(-16250354), this.a(-14275269));
            class_1799 st2 = z.stack();
            if (this.e > 0.4F && this.przejscie > 0.3F) {
               c.method_51427(st2, ix + 2, ry + 4);
               c.method_51431(tr, st2, ix + 2, ry + 4);
            }

            if (wLiscie && in(mx, my, ix, ry + 2, 20, 20)) {
               this.tooltip = st2;
            }

            String zy = "+" + cenaD(z.zysk);
            String kiedy = DATA.format(new Date(z.czas)) + " · " + temu(z.czas);
            int pw1 = Math.max(tr.method_1727(zy), tr.method_1727(kiedy));
            int prawa = wx + ww - 10;
            this.text(c, zy, prawa - tr.method_1727(zy), ry + 4, POZIOMY[0]);
            this.text(c, kiedy, prawa - tr.method_1727(kiedy), ry + 14, -10919566);
            int nx = ix + 26;
            int nw = prawa - pw1 - 10 - nx;
            this.text(c, this.przytnij(st2.method_63015(), nw), nx, ry + 4, -1512205);
            String d = cenaD(z.cena)
               + (z.ilosc > 1 ? " (" + z.ilosc + " szt.)" : "")
               + " · "
               + z.proby
               + (z.proby == 1 ? " próba" : (z.proby < 5 ? " próby" : " prób"));
            String stan = z.wystawiono ? "wystawiono za " + cenaD(z.cenaWystawienia) : (z.naPasku ? "na pasku · weź do ręki" : "czeka na przeniesienie");
            int dw2 = tr.method_1727(d);
            this.text(c, Draw.trim(tr, d, nw), nx, ry + 14, -865972);
            if (dw2 + 10 < nw) {
               this.text(c, Draw.trim(tr, "· " + stan, nw - dw2 - 4), nx + dw2 + 4, ry + 14, z.wystawiono ? POZIOMY[0] : -7629657);
            }
         }
      }

      if (lista.isEmpty()) {
         String a1 = Zakupy.wszystkie().isEmpty() ? "Brak zakupów" : "Nic nie pasuje";
         String a2 = "Snajper kupuje zaznaczone przedmioty po kliknięciu Start na rynku";
         this.text(c, a1, wx + (ww - tr.method_1727(a1)) / 2, top + (this.bb - top) / 2 - 6, -7629657);
         this.text(c, Draw.trim(tr, a2, ww - 16), wx + (ww - Math.min(tr.method_1727(a2), ww - 16)) / 2, top + (this.bb - top) / 2 + 6, -10919566);
      }

      c.method_44380();
      this.suwak(c, wx, ww, top, lista.size(), wLiscie);
      this.warstwa = 1.0F;
   }

   private void wybierzOferte(Wpis w) {
      this.wybranaOferta = w;
      this.detScrollCel = 0.0F;
      this.detScroll = 0.0F;
   }

   private void wybierzGrupe(BazaScreen.Grupa g) {
      this.wybranaGrupa = g;
      this.detScrollCel = 0.0F;
      this.detScroll = 0.0F;
   }

   private void wejdz(BazaScreen.Grupa g, int poziom) {
      if (g != null) {
         this.wybranaGrupa = g;
         this.otwarta = g;
         this.otwartaKlucz = g.klucz;
         this.filtr = poziom;
         this.scrollGrup = this.scrollCel;
         this.scroll = this.scrollCel = 0.0F;
         this.pasek = -1.0F;
         this.przejscie = 0.0F;
         this.kierunek = 1;
         this.wybranaOferta = null;
         this.przefiltruj();
      }
   }

   private void filtruj(int poziom) {
      this.filtr = poziom;
      this.scroll = this.scrollCel = 0.0F;
      this.pasek = -1.0F;
      this.przejscie = 0.4F;
      this.przefiltruj();
   }

   private void przefiltruj() {
      List<Wpis> l = new ArrayList<>();
      long teraz = System.currentTimeMillis();

      for (Wpis w : this.otwarta.oferty) {
         if (this.filtr < 0 || w.cena >= 0L && (w.aktywna(teraz) || this.otwarta.a.zHistorii) && this.otwarta.a.poziom(w.zaSztukeD()) == this.filtr) {
            l.add(w);
         }
      }

      this.widoczne = l;
      if (!this.widoczne.contains(this.wybranaOferta)) {
         this.wybierzOferte(this.widoczne.isEmpty() ? null : this.widoczne.get(0));
      }
   }

   private void wyjdz() {
      this.otwarta = null;
      this.otwartaKlucz = null;
      this.scroll = this.scrollCel = this.scrollGrup;
      this.pasek = -1.0F;
      this.przejscie = 0.0F;
      this.kierunek = -1;
      this.detScroll = this.detScrollCel = 0.0F;
   }

   private int rozmiar() {
      return this.otwarta != null ? this.widoczne.size() : this.grupy.size();
   }

   private Object element(int i) {
      return this.otwarta != null ? this.widoczne.get(i) : this.grupy.get(i);
   }

   private int wybranyIndeks() {
      return this.otwarta != null ? this.widoczne.indexOf(this.wybranaOferta) : this.grupy.indexOf(this.wybranaGrupa);
   }

   private void panel(class_332 c, int mx, int my) {
      class_327 tr = this.field_22793;
      long teraz = class_156.method_658();

      for (int i = 1; i <= 6; i++) {
         Draw.rect(c, this.x - i, this.y - i + 2, this.pw + i * 2, this.ph + i * 2, this.a(Draw.alpha(0, 34 - i * 3)));
      }

      Draw.box(c, this.x, this.y, this.pw, this.ph, this.a(-233827559), this.a(-14275269));
      c.method_25296(this.x + 1, this.y + 1, this.x + this.pw - 1, this.y + 36, this.a(-14933455), this.a(-15591904));
      c.method_25294(this.x + 1, this.y + 36, this.x + this.pw - 1, this.y + 36 + 1, this.a(-14275269));
      float blask = (float)(teraz % 3200L) / 3200.0F;
      int bx = this.x + 1 + (int)((this.pw + 120) * blask) - 60;

      for (int i = 0; i < this.pw - 2; i++) {
         float d = Math.abs(this.x + 1 + i - bx) / 60.0F;
         float k = Math.max(Math.max(0.0F, 1.0F - i / 140.0F), d < 1.0F ? (1.0F - d) * 0.7F : 0.0F);
         if (k > 0.0F) {
            c.method_25294(this.x + 1 + i, this.y + 36, this.x + 2 + i, this.y + 36 + 1, this.a(Draw.mix(-14275269, -13643391, k)));
         }
      }

      Draw.box(c, this.x + 8, this.y + 8, 20, 20, this.a(-15986924), this.a(-14910899));
      if (this.e > 0.4F) {
         c.method_51427(LOGO, this.x + 10, this.y + 10);
      }

      this.text(c, class_2561.method_43470("Skaner rynku").method_27692(class_124.field_1067).method_30937(), this.x + 34, this.y + 9, -1512205);
      String pod = this.grupy.size() + " przedmiotów · " + Skaner.ile() + " ofert w bazie" + (Skaner.nowe() > 0 ? " · +" + Skaner.nowe() + " nowych" : "");
      int cx = this.x + this.pw - 26;
      int cy = this.y + 10;
      boolean ch = in(mx, my, cx, cy, 16, 16);
      Draw.box(c, cx, cy, 16, 16, this.a(ch ? Draw.mix(-15986924, -1090725, 0.25F) : -15986924), this.a(ch ? -1090725 : -14275269));
      this.text(c, "×", cx + (16 - tr.method_1727("×")) / 2 + 1, cy + 4, ch ? -1090725 : -7629657);
      this.hots.add(new BazaScreen.Hot(cx, cy, 16, 16, this::method_25419));
      boolean on = Klikacz.dziala();
      String st = on ? "Klikacz: włączony" : "Klikacz: wyłączony";
      int stw = tr.method_1727(st) + 22;
      int stx = cx - 6 - stw;
      Draw.box(c, stx, cy, stw, 16, this.a(on ? Draw.mix(-15986924, -13643391, 0.12F) : -15986924), this.a(on ? -14910899 : -14275269));
      float puls = (float)(Math.sin(teraz / 200.0) * 0.5 + 0.5);
      this.text(c, "●", stx + 6, cy + 4, on ? Draw.mix(-14910899, -13643391, puls) : -10919566);
      this.text(c, st, stx + 16, cy + 4, on ? -1512205 : -7629657);
      String[] zak = new String[]{"Przedmioty", "Snajper " + Snajper.cele().size(), "Zakupy " + Zakupy.wszystkie().size()};
      int[] zx = new int[3];
      int[] zw = new int[3];
      int kx = stx - 8;

      for (int i = 2; i >= 0; i--) {
         zw[i] = tr.method_1727(zak[i]) + 14;
         kx -= zw[i];
         zx[i] = kx;
         kx -= 2;
      }

      if (this.zakladkaX < 0.0F) {
         this.zakladkaX = zx[zakladka] - this.x;
         this.zakladkaW = zw[zakladka];
      }

      this.zakladkaX = this.lerp(this.zakladkaX, zx[zakladka] - this.x, 18.0F);
      this.zakladkaW = this.lerp(this.zakladkaW, zw[zakladka], 18.0F);
      Draw.box(c, this.x + Math.round(this.zakladkaX), cy, Math.round(this.zakladkaW), 16, this.a(Draw.mix(-15986924, -865972, 0.25F)), this.a(-865972));

      for (int i = 0; i < 3; i++) {
         boolean hz = in(mx, my, zx[i], cy, zw[i], 16) && zakladka != i;
         if (hz) {
            Draw.rect(c, zx[i], cy, zw[i], 16, this.a(-12892839));
         }

         float pokr = clamp(1.0F - Math.abs(zx[i] - this.x - this.zakladkaX) / zw[i]);
         this.text(c, zak[i], zx[i] + 7, cy + 4, Draw.mix(hz ? -1512205 : -7629657, Draw.lighten(-865972), pokr));
         int nr = i;
         this.hots.add(new BazaScreen.Hot(zx[i], cy, zw[i], 16, () -> {
            if (zakladka != nr) {
               zakladka = nr;
               this.tScroll = this.tScrollCel = 0.0F;
               this.przejscie = 0.0F;
               this.kierunek = 1;
            }
         }));
      }

      this.text(c, Draw.trim(tr, pod, zx[0] - this.x - 40), this.x + 34, this.y + 21, -7629657);
      this.pasekSzukania(c, mx, my);
      this.chipy(c, mx, my);
      if (zakladka == 1) {
         this.snajperPanel(c, mx, my);
      } else if (zakladka == 2) {
         this.zakupyPanel(c, mx, my);
      } else if (this.wykres != null) {
         this.wykresPanel(c, mx, my);
      } else {
         this.lista(c, mx, my);
         this.warstwa = this.przejscie;
         if (this.otwarta != null) {
            this.szczegolyOferty(c, mx, my);
         } else {
            this.szczegolyGrupy(c, mx, my);
         }

         this.warstwa = 1.0F;
      }

      this.stopka(c, mx, my);
   }

   private void pasekSzukania(class_332 c, int mx, int my) {
      this.fokus = this.lerp(this.fokus, this.szukaj.method_25370() ? 1.0F : 0.0F, 14.0F);
      boolean h = in(mx, my, this.sx, this.ty, this.sw, 20);
      int obrys = Draw.mix(h ? -12892839 : -14275269, -13643391, this.fokus);
      Draw.box(c, this.sx, this.ty, this.sw, 20, this.a(-16250354), this.a(obrys));
      int ik = Draw.mix(-10919566, -13643391, this.fokus);
      Draw.rect(c, this.sx + 6, this.ty + 5, 7, 7, this.a(ik));
      c.method_25294(this.sx + 7, this.ty + 6, this.sx + 12, this.ty + 11, this.a(-16250354));
      c.method_25294(this.sx + 12, this.ty + 11, this.sx + 13, this.ty + 12, this.a(ik));
      c.method_25294(this.sx + 13, this.ty + 12, this.sx + 14, this.ty + 13, this.a(ik));
      c.method_25294(this.sx + 14, this.ty + 13, this.sx + 15, this.ty + 14, this.a(ik));
      this.hots.add(new BazaScreen.Hot(this.sx, this.ty, this.sw - 16, 20, () -> this.method_25395(this.szukaj)));
      if (!szukane.isEmpty()) {
         int kx = this.sx + this.sw - 15;
         boolean kh = in(mx, my, kx, this.ty + 4, 12, 12);
         this.text(c, "×", kx + 3, this.ty + 6, kh ? -1090725 : -10919566);
         this.hots.add(new BazaScreen.Hot(kx, this.ty + 4, 12, 12, () -> this.szukaj.method_1852("")));
      }
   }

   private void chipy(class_332 c, int mx, int my) {
      class_327 tr = this.field_22793;
      int cx = this.sx + this.sw + 4;
      int[] xs = new int[SORTY.length];
      int[] ws = new int[SORTY.length];

      for (int i = 0; i < SORTY.length; i++) {
         xs[i] = cx;
         ws[i] = tr.method_1727(SORTY[i]) + 14;
         cx += ws[i] + 3;
      }

      if (this.chipX < 0.0F) {
         this.chipX = xs[sort] - this.x;
         this.chipW = ws[sort];
      }

      this.chipX = this.lerp(this.chipX, xs[sort] - this.x, 18.0F);
      this.chipW = this.lerp(this.chipW, ws[sort], 18.0F);

      for (int i = 0; i < SORTY.length; i++) {
         boolean h = in(mx, my, xs[i], this.ty, ws[i], 20);
         Draw.box(c, xs[i], this.ty, ws[i], 20, this.a(h ? -15196630 : -15986924), this.a(h ? -12892839 : -14275269));
         int nr = i;
         this.hots.add(new BazaScreen.Hot(xs[i], this.ty, ws[i], 20, () -> {
            sort = nr;
            this.scrollCel = 0.0F;
         }));
      }

      Draw.box(c, this.x + Math.round(this.chipX), this.ty, Math.round(this.chipW), 20, this.a(-13643391), this.a(-13643391));

      for (int i = 0; i < SORTY.length; i++) {
         float pokrycie = clamp(1.0F - Math.abs(xs[i] - this.x - this.chipX) / ws[i]);
         this.text(c, SORTY[i], xs[i] + 7, this.ty + 6, Draw.mix(-7629657, -16379124, pokrycie));
      }
   }

   private void naglowekGrupy(class_332 c, int mx, int my) {
      class_327 tr = this.field_22793;
      int hy = this.by + 1;
      c.method_25294(this.lx + 1, hy, this.lx + this.lw - 1, hy + 22 - 1, this.a(-15591904));
      c.method_25294(this.lx + 1, hy + 22 - 1, this.lx + this.lw - 1, hy + 22, this.a(-14275269));
      String w = "‹ Wróć";
      int ww = tr.method_1727(w) + 12;
      boolean h = in(mx, my, this.lx + 4, hy + 3, ww, 15);
      Draw.box(c, this.lx + 4, hy + 3, ww, 15, this.a(h ? Draw.mix(-15986924, -13643391, 0.2F) : -15986924), this.a(h ? -13643391 : -14275269));
      this.text(c, w, this.lx + 10, hy + 7, h ? -13643391 : -7629657);
      this.hots.add(new BazaScreen.Hot(this.lx + 4, hy + 3, ww, 15, this::wyjdz));
      int cx = this.lx + ww + 8;

      for (int p = -1; p < 3; p++) {
         String l = p < 0 ? "Wszystkie " + this.otwarta.oferty.size() : Analiza.NAZWY[p] + " " + this.otwarta.a.ile[p];
         int cw = tr.method_1727(l) + 10;
         int kol = p < 0 ? -13643391 : POZIOMY[p];
         boolean wl = this.filtr == p;
         boolean ch = in(mx, my, cx, hy + 3, cw, 15);
         Draw.box(c, cx, hy + 3, cw, 15, this.a(wl ? Draw.mix(-15986924, kol, 0.3F) : (ch ? -15196630 : -15986924)), this.a(!wl && !ch ? -14275269 : kol));
         this.text(c, l, cx + 5, hy + 7, wl ? Draw.lighten(kol) : Draw.mix(-7629657, kol, 0.5F));
         int nr = p;
         this.hots.add(new BazaScreen.Hot(cx, hy + 3, cw, 15, () -> this.filtruj(nr)));
         cx += cw + 3;
      }

      if (this.otwarta.a.rynkowa > 0.0) {
         String info = "rynek " + krotkoD(this.otwarta.a.rynkowa);
         int iw = tr.method_1727(info);
         if (cx + 6 + iw < this.lx + this.lw - 8) {
            this.text(c, info, this.lx + this.lw - 8 - iw, hy + 7, -865972);
         }
      }
   }

   private void lista(class_332 c, int mx, int my) {
      class_327 tr = this.field_22793;
      Draw.box(c, this.lx, this.by, this.lw, this.bb - this.by, this.a(-15986924), this.a(-14275269));
      int top = this.by;
      if (this.otwarta != null) {
         this.naglowekGrupy(c, mx, my);
         top = this.by + 22;
      }

      int lh = this.bb - top;
      int n = this.rozmiar();
      int tresc = n * 24 + 4;
      float max = Math.max(0, tresc - lh);
      this.scrollCel = Math.max(0.0F, Math.min(max, this.scrollCel));
      this.scroll = this.lerp(this.scroll, this.scrollCel, 16.0F);
      if (n == 0) {
         String a1 = Skaner.ile() == 0 ? "Baza jest pusta" : "Nic nie pasuje";
         String a2 = Skaner.ile() == 0 ? "Otwórz rynek, a mod sam zeskanuje przedmioty" : "Spróbuj innego słowa";
         this.text(c, a1, this.lx + (this.lw - tr.method_1727(a1)) / 2, top + lh / 2 - 10, -7629657);
         String p2 = Draw.trim(tr, a2, this.lw - 12);
         this.text(c, p2, this.lx + (this.lw - tr.method_1727(p2)) / 2, top + lh / 2 + 2, -10919566);
      } else {
         this.warstwa = this.przejscie;
         int przes = Math.round((1.0F - this.przejscie) * 16.0F * this.kierunek);
         c.method_44379(this.lx + 1, top + 1, this.lx + this.lw - 1, this.bb - 1);
         int idx = this.wybranyIndeks();
         if (idx >= 0) {
            float cel = idx * 24;
            this.pasek = this.pasek < 0.0F ? cel : this.lerp(this.pasek, cel, 20.0F);
         }

         int pierwszy = Math.max(0, (int)(this.scroll / 24.0F) - 1);
         int ostatni = Math.min(n, pierwszy + lh / 24 + 3);
         boolean wLiscie = in(mx, my, this.lx, top, this.lw, lh);

         for (int i = pierwszy; i < ostatni; i++) {
            Object o = this.element(i);
            int ry = top + 2 + i * 24 - Math.round(this.scroll);
            boolean h = wLiscie && in(mx, my, this.lx + 1, ry, this.lw - 2, 24);
            float hv = this.lerp(this.hover.getOrDefault(o, 0.0F), h ? 1.0F : 0.0F, 18.0F);
            if (hv > 0.0F) {
               this.hover.put(o, hv);
            } else {
               this.hover.remove(o);
            }

            boolean wyb = i == idx;
            int tlo = wyb ? -15063754 : Draw.mix(-15986924, -15196630, hv);
            c.method_25294(this.lx + 2, ry, this.lx + this.lw - 2, ry + 24 - 1, this.a(tlo));
            c.method_25294(this.lx + 6, ry + 24 - 1, this.lx + this.lw - 6, ry + 24, this.a(285212671));
            int ix = this.lx + 6 + Math.round(hv * 2.0F) + przes;
            Wpis w = o instanceof BazaScreen.Grupa g ? g.wzor : (Wpis)o;
            class_1799 st = w.stack();
            Draw.box(c, ix, ry + 2, 20, 20, this.a(-16250354), this.a(wyb ? -14910899 : -14275269));
            if (this.e > 0.4F && this.przejscie > 0.3F) {
               c.method_51427(st, ix + 2, ry + 4);
               if (o instanceof BazaScreen.Grupa) {
                  c.method_51432(tr, st, ix + 2, ry + 4, "");
               } else {
                  c.method_51431(tr, st, ix + 2, ry + 4);
               }
            }

            if (wLiscie && in(mx, my, ix, ry + 2, 20, 20)) {
               this.tooltip = st;
            }

            int nx = ix + 26;
            int prawa = this.lx + this.lw - 8 + przes;
            if (o instanceof BazaScreen.Grupa g) {
               this.wierszGrupy(c, g, nx, ry, prawa, hv);
            } else {
               this.wierszOferty(c, w, nx, ry, prawa);
            }
         }

         if (idx >= 0) {
            int py0 = top + 2 + Math.round(this.pasek - this.scroll);
            c.method_25294(this.lx + 2, py0 + 3, this.lx + 4, py0 + 24 - 4, this.a(-13643391));
         }

         c.method_44380();
         this.warstwa = 1.0F;
         if (max > 0.0F) {
            int th = Math.max(18, lh * lh / tresc);
            int tyy = top + Math.round((lh - th) * (this.scroll / max));
            c.method_25294(this.lx + this.lw - 4, top + 2, this.lx + this.lw - 2, this.bb - 2, this.a(352321535));
            c.method_25294(this.lx + this.lw - 4, tyy + 2, this.lx + this.lw - 2, tyy + th - 2, this.a(wLiscie ? -7629657 : -12892839));
         }
      }
   }

   private void wierszGrupy(class_332 c, BazaScreen.Grupa g, int nx, int ry, int prawa, float hv) {
      class_327 tr = this.field_22793;
      Analiza an = g.a;
      String gora = an.rynkowa > 0.0 ? "Mid " + cenaD(an.rynkowa) : "brak ceny";
      boolean okazja = an.ile[0] > 0 && !an.zHistorii;
      String dol = okazja ? an.ile[0] + " × LOW · +" + krotkoD(an.najlepszyZysk) : an.aktywnych + " " + ceny(an.aktywnych) + " teraz";
      int strzalka = Math.round(hv * 3.0F);
      int pw1 = Math.max(tr.method_1727(gora), tr.method_1727(dol)) + 10;
      int nw = prawa - pw1 - 8 - nx;
      Wpis w = g.wzor;
      this.text(c, this.przytnij(w.stack().method_63015(), nw), nx, ry + 4, -1512205);
      String drugi;
      int kol;
      if (!w.enchanty.isEmpty()) {
         drugi = String.join(", ", w.enchanty);
         kol = -4875009;
      } else {
         drugi = g.oferty.size() + (g.oferty.size() == 1 ? " oferta" : (g.oferty.size() < 5 ? " oferty" : " ofert")) + " · " + temu(g.ostatni);
         kol = -10919566;
      }

      this.text(c, Draw.trim(tr, drugi, nw), nx, ry + 14, kol);
      this.text(c, gora, prawa - 10 - tr.method_1727(gora), ry + 4, an.rynkowa > 0.0 ? -865972 : -10919566);
      this.text(c, dol, prawa - 10 - tr.method_1727(dol), ry + 14, okazja ? POZIOMY[0] : -10919566);
      this.text(c, "›", prawa - 4 + strzalka - 2, ry + 9, Draw.mix(-10919566, -13643391, hv));
   }

   private void wierszOferty(class_332 c, Wpis w, int nx, int ry, int prawa) {
      class_327 tr = this.field_22793;
      Analiza an = this.otwarta.a;
      boolean aktywna = w.aktywna(System.currentTimeMillis());
      String gora = w.cena >= 0L ? cenaD(w.zaSztukeD()) + (w.ilosc > 1 ? " / szt." : "") : "brak ceny";
      String dol = aktywna ? temu(w.ostatni) : "wygasła";
      this.text(c, gora, nx, ry + 4, w.cena < 0L ? -10919566 : (aktywna ? -865972 : Draw.mix(-865972, -15986924, 0.5F)));
      if (w.cena >= 0L) {
         int p = an.poziom(w.zaSztukeD());
         String znak = Analiza.NAZWY[p].toUpperCase(Locale.ROOT);
         int bx = nx + tr.method_1727(gora) + 5;
         int bw = tr.method_1727(znak) + 6;
         int kol = aktywna ? POZIOMY[p] : -12892839;
         Draw.box(c, bx, ry + 3, bw, 10, this.a(Draw.mix(-15986924, kol, 0.25F)), this.a(kol));
         this.text(c, znak, bx + 3, ry + 4, Draw.lighten(kol));
         double zysk = an.zysk(w.zaSztukeD()) * w.ilosc;
         String prawy = p == 0 ? "zysk +" + krotkoD(zysk) : roznica(w.zaSztukeD(), an.rynkowa);
         int kp = p == 0 ? POZIOMY[0] : kolorRoznicy(prawy);
         if (!prawy.isEmpty()) {
            this.text(c, prawy, prawa - tr.method_1727(prawy), ry + 4, aktywna ? kp : -10919566);
         }
      }

      this.text(c, dol, prawa - tr.method_1727(dol), ry + 14, aktywna ? -10919566 : Draw.mix(-8769997, -1090725, 0.4F));
      String opisLinia = "";

      for (String l : w.lore) {
         String low = l.toLowerCase(Locale.ROOT);
         if (!l.isBlank() && !low.contains("koszt") && !low.contains("$")) {
            if (low.contains("wystawi")) {
               opisLinia = l.trim();
               break;
            }

            if (opisLinia.isEmpty()) {
               opisLinia = l.trim();
            }
         }
      }

      if (opisLinia.isEmpty()) {
         opisLinia = "widziana " + w.razy + "×";
      }

      boolean enchanty = w.kolorowa() && !w.enchanty.isEmpty();
      this.text(
         c,
         Draw.trim(tr, enchanty ? String.join(", ", w.enchanty) : opisLinia, prawa - tr.method_1727(dol) - 8 - nx),
         nx,
         ry + 14,
         enchanty ? -4875009 : -7629657
      );
   }

   private void ikonaDuza(class_332 c, Wpis w, boolean ilosc, int mx, int my) {
      class_1799 st = w.stack();
      Draw.box(c, this.dx + 8, this.by + 8, 38, 38, this.a(-16250354), this.a(-14910899));
      if (this.e > 0.4F && this.przejscie > 0.3F) {
         c.method_51448().method_22903();
         c.method_51448().method_46416(this.dx + 11, this.by + 11, 0.0F);
         c.method_51448().method_22905(2.0F, 2.0F, 1.0F);
         c.method_51427(st, 0, 0);
         c.method_51448().method_22909();
         if (ilosc && w.ilosc > 1) {
            String il = "×" + w.ilosc;
            c.method_51448().method_22903();
            c.method_51448().method_46416(0.0F, 0.0F, 300.0F);
            this.text(c, il, this.dx + 44 - this.field_22793.method_1727(il), this.by + 36, -1512205);
            c.method_51448().method_22909();
         }
      }

      if (in(mx, my, this.dx + 8, this.by + 8, 38, 38)) {
         this.tooltip = st;
      }

      List<class_5481> nazwa = this.field_22793.method_1728(st.method_63015(), this.dw - 60);

      for (int i = 0; i < Math.min(3, nazwa.size()); i++) {
         this.text(c, nazwa.get(i), this.dx + 52, this.by + 9 + i * 10, -1512205);
      }

      this.text(c, Draw.trim(this.field_22793, w.id, this.dw - 60), this.dx + 52, this.by + 9 + Math.min(3, nazwa.size()) * 10, -10919566);
   }

   private void szczegolyGrupy(class_332 c, int mx, int my) {
      class_327 tr = this.field_22793;
      int dh = this.bb - this.by;
      Draw.box(c, this.dx, this.by, this.dw, dh, this.a(-15657959), this.a(-14275269));
      BazaScreen.Grupa g = this.wybranaGrupa;
      if (g == null) {
         String s = "Wybierz przedmiot";
         this.text(c, s, this.dx + (this.dw - tr.method_1727(s)) / 2, this.by + dh / 2 - 4, -10919566);
      } else {
         Analiza an = g.a;
         this.ikonaDuza(c, g.wzor, false, mx, my);
         boolean chce = Snajper.chce(g.klucz);
         int gx = this.dx + this.dw - 22;
         boolean hg = in(mx, my, gx, this.by + 34, 14, 14);
         Draw.box(
            c,
            gx,
            this.by + 34,
            14,
            14,
            this.a(chce ? Draw.mix(-15986924, -865972, 0.35F) : (hg ? -15196630 : -15986924)),
            this.a(!chce && !hg ? -14275269 : -865972)
         );
         this.text(c, chce ? "★" : "☆", gx + 3, this.by + 37, chce ? -865972 : (hg ? -865972 : -10919566));
         this.hots.add(new BazaScreen.Hot(gx, this.by + 34, 14, 14, () -> Snajper.przelacz(g.klucz)));
         int sy = this.by + 52;
         int kw = (this.dw - 16 - 6) / 3;

         for (int p = 0; p < 3; p++) {
            int kx = this.dx + 8 + p * (kw + 3);
            boolean h = in(mx, my, kx, sy, kw, 40) && an.ile[p] > 0;
            int kol = POZIOMY[p];
            Draw.box(c, kx, sy, kw, 40, this.a(Draw.mix(-15986924, kol, h ? 0.3F : 0.12F)), this.a(h ? kol : Draw.mix(-14275269, kol, 0.5F)));
            this.text(c, Analiza.NAZWY[p], kx + 4, sy + 4, Draw.lighten(kol));
            String v;
            if (an.rynkowa <= 0.0) {
               v = "—";
            } else if (p == 0) {
               v = an.jestLow() ? "≤" + krotkoD(an.progLow) : "brak";
            } else if (p == 1) {
               v = krotkoD(an.rynkowa);
            } else {
               v = ">" + krotkoD(an.progOver);
            }

            this.text(c, Draw.trim(tr, v, kw - 8), kx + 4, sy + 16, an.rynkowa > 0.0 ? -1512205 : -10919566);
            String il = an.ile[p] + "×";
            this.text(c, Draw.trim(tr, il, kw - 6), kx + 4, sy + 28, an.ile[p] > 0 && p == 0 ? kol : -10919566);
            if (an.ile[p] > 0) {
               int nr = p;
               this.hots.add(new BazaScreen.Hot(kx, sy, kw, 40, () -> this.wejdz(g, nr)));
            }
         }

         sy += 45;
         if (an.rynkowa <= 0.0) {
            sy = this.wiersz(c, "Cena", "brak w lore", -10919566, sy);
         } else {
            sy = this.wiersz(c, "Cena rynku", cenaD(an.rynkowa), POZIOMY[1], sy);
            sy = this.wiersz(c, "Średnia Mid", cenaD(an.sredniaMid), -1512205, sy);
            if (an.jestLow()) {
               sy = this.wiersz(c, "Low · kupuj do", cenaD(an.progLow), POZIOMY[0], sy);
               sy = this.wiersz(c, "Zysk przy Low", "+" + krotkoD(an.naReke - an.progLow) + " (" + Math.round(an.profit * 100.0) + "%)", POZIOMY[0], sy);
            }
         }

         String z = an.zHistorii ? "0 · z historii" : an.aktywnych + " / " + an.wszystkich;
         sy = this.wiersz(c, "Aktywne oferty", z, an.zHistorii ? -10919566 : -1512205, sy);
         int bw = this.dw - 16;
         int byy = sy + 2;
         boolean h = in(mx, my, this.dx + 8, byy, bw, 15);
         int bw2 = (bw - 3) / 2;
         Draw.box(c, this.dx + 8, byy, bw2, 15, this.a(h && mx < this.dx + 8 + bw2 ? Draw.lighten(-13643391) : -13643391), this.a(-13643391));
         String l = "Ceny  ›";
         this.text(c, l, this.dx + 8 + (bw2 - tr.method_1727(l)) / 2, byy + 4, -16379124);
         this.hots.add(new BazaScreen.Hot(this.dx + 8, byy, bw2, 15, () -> this.wejdz(g, -1)));
         int wx2 = this.dx + 8 + bw2 + 3;
         boolean hw = in(mx, my, wx2, byy, bw - bw2 - 3, 15);
         Draw.box(c, wx2, byy, bw - bw2 - 3, 15, this.a(hw ? Draw.mix(-15986924, -865972, 0.35F) : Draw.mix(-15986924, -865972, 0.18F)), this.a(-865972));
         String lw2 = "Wykres  ›";
         this.text(c, lw2, wx2 + (bw - bw2 - 3 - tr.method_1727(lw2)) / 2, byy + 4, Draw.lighten(-865972));
         this.hots.add(new BazaScreen.Hot(wx2, byy, bw - bw2 - 3, 15, () -> this.otworzWykres(g)));
         sy = byy + 20;
         long teraz = System.currentTimeMillis();
         int oh = this.bb - 4 - sy;
         int tresc = g.oferty.size() * 10;
         float max = Math.max(0, tresc - oh);
         this.detScrollCel = Math.max(0.0F, Math.min(max, this.detScrollCel));
         this.detScroll = this.lerp(this.detScroll, this.detScrollCel, 16.0F);
         c.method_44379(this.dx + 1, sy - 1, this.dx + this.dw - 1, this.bb - 3);

         for (int i = 0; i < g.oferty.size(); i++) {
            int ly = sy + i * 10 - Math.round(this.detScroll);
            if (ly >= sy - 10 && ly <= this.bb) {
               Wpis w = g.oferty.get(i);
               boolean akt = w.aktywna(teraz);
               if (w.cena >= 0L) {
                  int p = an.poziom(w.zaSztukeD());
                  int kol = akt ? POZIOMY[p] : -10919566;
                  c.method_25294(this.dx + 9, ly + 2, this.dx + 12, ly + 5, this.a(kol));
                  this.text(c, cenaD(w.zaSztukeD()), this.dx + 16, ly, kol);
               } else {
                  this.text(c, "—", this.dx + 16, ly, -10919566);
               }

               String t = akt ? temu(w.ostatni) : "wygasła";
               this.text(c, t, this.dx + this.dw - 9 - tr.method_1727(t), ly, -10919566);
            }
         }

         c.method_44380();
         if (max > 0.0F) {
            int th = Math.max(12, oh * oh / tresc);
            int tyy = sy + Math.round((oh - th) * (this.detScroll / max));
            c.method_25294(this.dx + this.dw - 4, tyy, this.dx + this.dw - 2, tyy + th, this.a(-12892839));
         }
      }
   }

   private void szczegolyOferty(class_332 c, int mx, int my) {
      class_327 tr = this.field_22793;
      int dh = this.bb - this.by;
      Draw.box(c, this.dx, this.by, this.dw, dh, this.a(-15657959), this.a(-14275269));
      Wpis w = this.wybranaOferta;
      if (w != null) {
         this.ikonaDuza(c, w, true, mx, my);
         int sy = this.by + 54;
         if (w.ilosc > 1) {
            sy = this.wiersz(c, "Cena za szt.", w.cena >= 0L ? cenaD(w.zaSztukeD()) : "brak w lore", w.cena >= 0L ? -865972 : -10919566, sy);
            sy = this.wiersz(c, "Ilość", w.ilosc + " szt.", -1512205, sy);
            if (w.cena >= 0L) {
               sy = this.wiersz(c, "Za całość", cena(w.cena), -1512205, sy);
            }
         } else {
            sy = this.wiersz(c, "Cena", w.cena >= 0L ? cena(w.cena) : "brak w lore", w.cena >= 0L ? -865972 : -10919566, sy);
         }

         if (w.cena >= 0L) {
            int p = this.otwarta.a.poziom(w.zaSztukeD());
            sy = this.wiersz(c, "Poziom", Analiza.NAZWY[p] + " price", POZIOMY[p], sy);
            String r = roznica(w.zaSztukeD(), this.otwarta.a.rynkowa);
            if (!r.isEmpty()) {
               sy = this.wiersz(c, "Do ceny rynku", r, kolorRoznicy(r), sy);
            }

            double zysk = this.otwarta.a.zysk(w.zaSztukeD()) * w.ilosc;
            if (this.otwarta.a.rynkowa > 0.0) {
               sy = this.wiersz(c, "Zysk po 3%", (zysk >= 0.0 ? "+" : "−") + cenaD(Math.abs(zysk)), zysk > 0.0 ? POZIOMY[0] : POZIOMY[2], sy);
            }
         }

         long teraz = System.currentTimeMillis();
         if (w.wygasa > 0L) {
            sy = this.wiersz(c, "Wygasa", w.wygasa > teraz ? "za " + czas(w.wygasa - teraz) : "wygasła", w.wygasa > teraz ? -1512205 : -1090725, sy);
         }

         sy = this.wiersz(c, "Widziana", w.razy + "×", -1512205, sy);
         sy = this.wiersz(c, "Ostatnio", temu(w.ostatni), -13643391, sy);
         c.method_25294(this.dx + 8, sy + 2, this.dx + this.dw - 8, sy + 3, this.a(-14275269));
         sy += 8;
         if (this.opisDla != w || this.opisSzer != this.dw - 18) {
            this.opisDla = w;
            this.opisSzer = this.dw - 18;
            List<class_5481> linie = new ArrayList<>();
            List<class_2561> tip = class_437.method_25408(this.field_22787, w.stack());

            for (int i = 1; i < tip.size(); i++) {
               linie.addAll(tr.method_1728((class_5348)tip.get(i), this.opisSzer));
            }

            this.opis = linie;
         }

         int oh = this.bb - 4 - sy;
         int tresc = this.opis.size() * 10;
         float max = Math.max(0, tresc - oh);
         this.detScrollCel = Math.max(0.0F, Math.min(max, this.detScrollCel));
         this.detScroll = this.lerp(this.detScroll, this.detScrollCel, 16.0F);
         if (this.opis.isEmpty()) {
            this.text(c, "Brak opisu", this.dx + 9, sy, -10919566);
         } else {
            c.method_44379(this.dx + 1, sy - 1, this.dx + this.dw - 1, this.bb - 3);

            for (int i = 0; i < this.opis.size(); i++) {
               int ly = sy + i * 10 - Math.round(this.detScroll);
               if (ly >= sy - 10 && ly <= this.bb) {
                  this.text(c, this.opis.get(i), this.dx + 9, ly, -5723992);
               }
            }

            c.method_44380();
            if (max > 0.0F) {
               int th = Math.max(12, oh * oh / tresc);
               int tyy = sy + Math.round((oh - th) * (this.detScroll / max));
               c.method_25294(this.dx + this.dw - 4, tyy, this.dx + this.dw - 2, tyy + th, this.a(-12892839));
            }
         }
      }
   }

   private int wiersz(class_332 c, String etykieta, String wartosc, int kolor, int wy) {
      this.text(c, etykieta, this.dx + 9, wy, -7629657);
      String v = Draw.trim(this.field_22793, wartosc, this.dw - 30 - this.field_22793.method_1727(etykieta));
      this.text(c, v, this.dx + this.dw - 9 - this.field_22793.method_1727(v), wy, kolor);
      return wy + 11;
   }

   private void stopka(class_332 c, int mx, int my) {
      class_327 tr = this.field_22793;
      int fy = this.y + this.ph - 22;
      c.method_25294(this.x + 1, fy, this.x + this.pw - 1, fy + 1, this.a(-14275269));
      int pfx = this.x + this.pw - 8 - 100 - 8 - 104;
      String pl = "Profit";
      this.text(c, pl, pfx, fy + 7, -7629657);
      int ppx = pfx + tr.method_1727(pl) + 5;
      boolean hm = in(mx, my, ppx, fy + 4, 14, 14);
      Draw.box(c, ppx, fy + 4, 14, 14, this.a(hm ? -15196630 : -15986924), this.a(hm ? -13643391 : -14275269));
      this.text(c, "−", ppx + 4, fy + 7, hm ? -13643391 : -7629657);
      this.hots.add(new BazaScreen.Hot(ppx, fy + 4, 14, 14, () -> Ustawienia.profit(Ustawienia.profit() - 5)));
      int pvw = 30;
      boolean hpv = in(mx, my, ppx + 16, fy + 4, pvw, 14);
      Draw.box(c, ppx + 16, fy + 4, pvw, 14, this.a(-16250354), this.a(this.profitPole.method_25370() ? -865972 : (hpv ? -12892839 : -14910899)));
      this.profitPole.method_46421(ppx + 19);
      this.profitPole.method_46419(fy + 7);
      this.profitPole.method_25358(pvw - 12);
      this.text(c, "%", ppx + 16 + pvw - 9, fy + 7, POZIOMY[0]);
      this.hots.add(new BazaScreen.Hot(ppx + 16, fy + 4, pvw, 14, () -> {
         this.method_25395(this.profitPole);
         this.profitPole.method_1872(false);
         this.zostawFokus = true;
      }));
      int pplus = ppx + 16 + pvw + 2;
      boolean hp = in(mx, my, pplus, fy + 4, 14, 14);
      Draw.box(c, pplus, fy + 4, 14, 14, this.a(hp ? -15196630 : -15986924), this.a(hp ? -13643391 : -14275269));
      this.text(c, "+", pplus + 4, fy + 7, hp ? -13643391 : -7629657);
      this.hots.add(new BazaScreen.Hot(pplus, fy + 4, 14, 14, () -> Ustawienia.profit(Ustawienia.profit() + 5)));
      this.profitX = pfx;
      this.profitW = pplus + 14 - pfx;
      String info = zakladka == 1
         ? "Kupuje tylko po Start na rynku · cena Low z profitu"
         : (
            zakladka == 2
               ? "Historia zakupów snajpera"
               : (
                  this.wykres != null
                     ? "Kółko: zoom (nad cenami: pion) · przeciągnij · 2× klik: reset"
                     : (this.otwarta != null ? "1-4 filtr · ← wróć · ↑↓ wybór" : this.grupy.size() + " przedm. · Enter ceny · X zamyka")
               )
         );
      this.text(c, Draw.trim(tr, info, pfx - this.x - 18), this.x + 10, fy + 7, -10919566);
      boolean zakupowy = zakladka == 2;
      boolean uzbrojony = class_156.method_658() < (zakupowy ? this.wyczyscZakupyDo : this.wyczyscDo);
      this.czysc = this.lerp(this.czysc, uzbrojony ? 1.0F : 0.0F, 14.0F);
      String l = uzbrojony ? "Na pewno?" : (zakupowy ? "Wyczyść zakupy" : "Wyczyść bazę");
      int bw = Math.max(80, tr.method_1727(l) + 12);
      int bx = this.x + this.pw - bw - 8;
      int byy = fy + 4;
      boolean h = in(mx, my, bx, byy, bw, 14);
      int tlo = Draw.mix(h ? Draw.mix(-15986924, -1090725, 0.2F) : -15986924, -1090725, this.czysc);
      Draw.box(c, bx, byy, bw, 14, this.a(tlo), this.a(!h && !uzbrojony ? -8769997 : -1090725));
      this.text(c, l, bx + (bw - tr.method_1727(l)) / 2, byy + 3, Draw.mix(-1090725, -16379124, this.czysc));
      this.hots.add(new BazaScreen.Hot(bx, byy, bw, 14, () -> {
         if (zakladka == 2) {
            if (class_156.method_658() < this.wyczyscZakupyDo) {
               Zakupy.wyczysc();
               this.wyczyscZakupyDo = 0L;
            } else {
               this.wyczyscZakupyDo = class_156.method_658() + 3000L;
            }
         } else {
            if (class_156.method_658() < this.wyczyscDo) {
               Skaner.wyczysc();
               this.wyczyscDo = 0L;
               this.wybranaGrupa = null;
               if (this.otwarta != null) {
                  this.wyjdz();
               }
            } else {
               this.wyczyscDo = class_156.method_658() + 3000L;
            }
         }
      }));
   }

   static String cena(long v) {
      String s = Long.toString(v);
      StringBuilder sb = new StringBuilder("$");

      for (int i = 0; i < s.length(); i++) {
         if (i > 0 && (s.length() - i) % 3 == 0) {
            sb.append(',');
         }

         sb.append(s.charAt(i));
      }

      return sb.toString();
   }

   public static String cenaD(double v) {
      if (v < 0.0) {
         return "—";
      } else {
         return v < 100.0 && Math.abs(v - Math.rint(v)) > 0.005 ? String.format(Locale.ROOT, "$%.2f", v) : cena(Math.round(v));
      }
   }

   public static String krotkoD(double v) {
      return v < 1000.0 ? cenaD(v) : krotko(Math.round(v));
   }

   static String czas(long ms) {
      long m = ms / 60000L;
      if (m < 60L) {
         return Math.max(1L, m) + " min";
      }

      long h = m / 60L;
      return h < 24L ? h + " h " + m % 60L + " min" : h / 24L + " d " + h % 24L + " h";
   }

   static String krotko(long v) {
      if (v < 0L) {
         return "—";
      }

      if (v < 1000L) {
         return cena(v);
      }

      String[] suf = new String[]{"k", "m", "b"};
      double d = v;

      int i;
      for (i = -1; d >= 1000.0 && i < 2; i++) {
         d /= 1000.0;
      }

      String t = d >= 100.0 ? String.format(Locale.ROOT, "%.0f", d) : String.format(Locale.ROOT, "%.1f", d);
      if (t.endsWith(".0")) {
         t = t.substring(0, t.length() - 2);
      }

      return "$" + t + suf[i];
   }

   static String ceny(int n) {
      if (n == 1) {
         return "cena";
      }

      int r = n % 10;
      int s = n % 100;
      return r < 2 || r > 4 || s >= 12 && s <= 14 ? "cen" : "ceny";
   }

   static String roznica(double cena, double srednia) {
      if (!(cena < 0.0) && !(srednia <= 0.0)) {
         long p = Math.round((cena - srednia) * 100.0 / srednia);
         return p == 0L ? "normalna" : (p > 0L ? "+" : "−") + Math.abs(p) + "%";
      } else {
         return "";
      }
   }

   static int kolorRoznicy(String r) {
      return r.startsWith("+") ? -1082517 : (r.startsWith("−") ? -10755185 : -7629657);
   }

   static String temu(long t) {
      long s = (System.currentTimeMillis() - t) / 1000L;
      if (s < 5L) {
         return "teraz";
      }

      if (s < 60L) {
         return s + " s temu";
      }

      long m = s / 60L;
      if (m < 60L) {
         return m + " min temu";
      }

      long h = m / 60L;
      if (h < 24L) {
         return h + " h temu";
      }

      long d = h / 24L;
      return d + (d == 1L ? " dzień temu" : " dni temu");
   }

   public boolean method_25402(double mx, double my, int button) {
      if (this.zamykanie >= 0L) {
         return true;
      }

      if (button == 1 && zakladka == 0 && this.wykres != null) {
         this.zamknijWykres();
         return true;
      }

      if (button == 1 && zakladka == 0 && this.otwarta != null) {
         this.wyjdz();
         return true;
      }

      for (int i = this.hots.size() - 1; i >= 0; i--) {
         BazaScreen.Hot h = this.hots.get(i);
         if (in(mx, my, h.x, h.y, h.w, h.h)) {
            this.zostawFokus = false;
            h.akcja.run();
            if (!this.zostawFokus && !in(mx, my, this.sx, this.ty, this.sw, 20)) {
               this.method_25395(null);
            }

            this.zostawFokus = false;
            return true;
         }
      }

      if (zakladka != 0) {
         if (super.method_25402(mx, my, button)) {
            return true;
         }

         this.method_25395(null);
         return false;
      } else if (this.wykres != null) {
         if (button == 0 && this.wykres.klik(mx, my)) {
            this.method_25395(null);
            return true;
         }

         if (super.method_25402(mx, my, button)) {
            return true;
         }

         this.method_25395(null);
         return false;
      } else {
         int top = this.otwarta != null ? this.by + 22 : this.by;
         if (in(mx, my, this.lx, top, this.lw, this.bb - top) && this.rozmiar() > 0) {
            int i = (int)((my - top - 2.0 + this.scroll) / 24.0);
            if (i >= 0 && i < this.rozmiar()) {
               Object o = this.element(i);
               long teraz = class_156.method_658();
               boolean podwojny = o == this.ostatnioKlikniety && teraz - this.ostatniKlik < 350L;
               this.ostatniKlik = teraz;
               this.ostatnioKlikniety = o;
               if (o instanceof BazaScreen.Grupa g) {
                  if (podwojny) {
                     this.wejdz(g, -1);
                  } else {
                     this.wybierzGrupe(g);
                  }
               } else {
                  this.wybierzOferte((Wpis)o);
               }
            }

            this.method_25395(null);
            return true;
         } else {
            if (super.method_25402(mx, my, button)) {
               return true;
            }

            this.method_25395(null);
            return false;
         }
      }
   }

   public boolean method_25406(double mx, double my, int button) {
      if (this.wykres != null) {
         this.wykres.puszczony();
      }

      return super.method_25406(mx, my, button);
   }

   public boolean method_25401(double mx, double my, double horizontal, double vertical) {
      if (zakladka != 0 && in(mx, my, this.lx, this.by, this.dx + this.dw - this.lx, this.bb - this.by)) {
         this.tScrollCel -= (float)(vertical * 24.0 * 1.5);
         return true;
      } else if (zakladka == 0 && this.wykres != null && this.wykres.kolko(mx, my, vertical)) {
         return true;
      } else if (in(mx, my, this.profitX, this.y + this.ph - 22, this.profitW, 22)) {
         Ustawienia.profit(Ustawienia.profit() + (vertical > 0.0 ? 5 : -5));
         return true;
      } else if (in(mx, my, this.lx, this.by, this.lw, this.bb - this.by)) {
         this.scrollCel -= (float)(vertical * 24.0 * 1.5);
         return true;
      } else if (in(mx, my, this.dx, this.by, this.dw, this.bb - this.by)) {
         this.detScrollCel -= (float)(vertical * 20.0);
         return true;
      } else {
         return super.method_25401(mx, my, horizontal, vertical);
      }
   }

   private void przesun(int o) {
      int n = this.rozmiar();
      if (n != 0) {
         int i = Math.max(0, Math.min(n - 1, this.wybranyIndeks() + o));
         Object el = this.element(i);
         if (el instanceof BazaScreen.Grupa g) {
            this.wybierzGrupe(g);
         } else {
            this.wybierzOferte((Wpis)el);
         }

         int lh = this.bb - (this.otwarta != null ? this.by + 22 : this.by);
         float ry = i * 24;
         if (ry < this.scrollCel) {
            this.scrollCel = ry;
         }

         if (ry + 24.0F + 4.0F > this.scrollCel + lh) {
            this.scrollCel = ry + 24.0F + 4.0F - lh;
         }
      }
   }

   public boolean method_25404(int key, int scan, int mod) {
      if (this.profitPole.method_25370()) {
         if (key != 257 && key != 335 && key != 256) {
            return super.method_25404(key, scan, mod);
         }

         this.method_25395(null);
         return true;
      } else if (zakladka != 0 && !this.szukaj.method_25370()) {
         if (key != 256 && !RynekKlikaczClient.baza.method_1417(key, scan)) {
            return super.method_25404(key, scan, mod);
         }

         this.method_25419();
         return true;
      } else if (this.wykres != null && this.zakres.method_25370()) {
         if (key == 257 || key == 335) {
            this.zastosujZakres();
            return true;
         } else if (key == 256) {
            this.method_25395(null);
            return true;
         } else {
            return super.method_25404(key, scan, mod);
         }
      } else if (this.wykres != null && !this.szukaj.method_25370()) {
         if (key == 256 || key == 259 || key == 263) {
            this.zamknijWykres();
            return true;
         } else if (RynekKlikaczClient.baza.method_1417(key, scan)) {
            this.method_25419();
            return true;
         } else {
            return true;
         }
      } else {
         if (key == 265 || key == 264) {
            this.przesun(key == 265 ? -1 : 1);
            return true;
         }

         if (key == 266 || key == 267) {
            this.przesun((key == 266 ? -1 : 1) * Math.max(1, (this.bb - this.by) / 24));
            return true;
         }

         if (key == 70 && method_25441()) {
            this.method_25395(this.szukaj);
            return true;
         }

         if (this.szukaj.method_25370()) {
            if (key != 256 && key != 257 && key != 335) {
               return super.method_25404(key, scan, mod);
            }

            this.method_25395(null);
            return true;
         } else if (this.otwarta != null || key != 257 && key != 335 && key != 262) {
            if (this.otwarta != null && key >= 49 && key <= 52) {
               this.filtruj(key - 49 - 1);
               return true;
            }

            if (this.otwarta == null || key != 256 && key != 259 && key != 263) {
               if (RynekKlikaczClient.baza.method_1417(key, scan)) {
                  this.method_25419();
                  return true;
               } else {
                  return super.method_25404(key, scan, mod);
               }
            } else {
               this.wyjdz();
               return true;
            }
         } else {
            this.wejdz(this.wybranaGrupa, -1);
            return true;
         }
      }
   }

   public boolean method_25400(char ch, int mod) {
      if (this.zamykanie >= 0L) {
         return true;
      }

      if (this.profitPole.method_25370()) {
         return this.profitPole.method_25400(ch, mod);
      }

      if (zakladka == 0 && this.wykres != null && !this.szukaj.method_25370()) {
         if (!this.zakres.method_25370() && ch > ' ') {
            this.method_25395(this.zakres);
         }

         return this.zakres.method_25400(ch, mod);
      } else if (zakladka == 0 && this.otwarta != null && !this.szukaj.method_25370() && ch >= '1' && ch <= '4') {
         return true;
      } else if (!this.szukaj.method_25370() && ch > ' ') {
         this.method_25395(this.szukaj);
         return this.szukaj.method_25400(ch, mod);
      } else {
         return super.method_25400(ch, mod);
      }
   }

   public void method_25419() {
      if (this.zamykanie < 0L) {
         this.zamykanie = class_156.method_658();
         this.method_25395(null);
      }
   }

   public void method_25432() {
      Skaner.zapisz();
      super.method_25432();
   }

   private static final class Grupa {
      String klucz;
      Wpis wzor;
      final List<Wpis> oferty = new ArrayList<>();
      final List<Long> ceny = new ArrayList<>();
      long suma;
      long ostatni;
      Analiza a;

      void dodaj(Wpis w) {
         this.oferty.add(w);
         if (this.wzor == null || w.ostatni > this.wzor.ostatni) {
            this.wzor = w;
         }

         this.ostatni = Math.max(this.ostatni, w.ostatni);
         if (w.cena >= 0L) {
            this.ceny.add(w.zaSztuke());
         }
      }

      double rynkowa() {
         return this.a == null ? -1.0 : this.a.rynkowa;
      }
   }

   private record Hot(int x, int y, int w, int h, Runnable akcja) {
   }
}
