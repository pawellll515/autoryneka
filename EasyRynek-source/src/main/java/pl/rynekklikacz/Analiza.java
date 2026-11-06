package pl.rynekklikacz;

import java.util.ArrayList;
import java.util.List;

public final class Analiza {
   public static final int LOW = 0;
   public static final int MID = 1;
   public static final int OVER = 2;
   public static final String[] NAZWY = new String[]{"Low", "Mid", "Over"};
   public static final double PODATEK = 0.03;
   public static final double MARGINES_OVER = 0.1;
   private static final double OKNO = 0.1;
   public final int[] ile = new int[3];
   public final int wszystkich;
   public final int aktywnych;
   public final boolean zHistorii;
   public final double rynkowa;
   public final double naReke;
   public final double progLow;
   public final double progOver;
   public final double profit;
   public final double sredniaMid;
   public final double najtansza;
   public final double najlepszyZysk;

   public Analiza(List<Wpis> oferty, int profitProc) {
      long teraz = System.currentTimeMillis();
      List<Double> akt = new ArrayList<>();
      List<Double> wsz = new ArrayList<>();

      for (Wpis w : oferty) {
         double c = w.zaSztukeD();
         if (!(c <= 0.0)) {
            wsz.add(c);
            if (w.aktywna(teraz)) {
               akt.add(c);
            }
         }
      }

      this.wszystkich = wsz.size();
      this.aktywnych = akt.size();
      this.zHistorii = akt.isEmpty() && !wsz.isEmpty();
      List<Double> baza = akt.isEmpty() ? wsz : akt;
      if (baza.isEmpty()) {
         this.rynkowa = this.naReke = this.progLow = this.progOver = this.profit = this.sredniaMid = this.najtansza = this.najlepszyZysk = -1.0;
      } else {
         double[] p = baza.stream().mapToDouble(Double::doubleValue).sorted().toArray();
         this.najtansza = p[0];
         this.rynkowa = podloga(p);
         this.naReke = this.rynkowa * 0.97;
         this.profit = profitProc / 100.0 * (1.0 + 0.1 * Math.log10(Math.max(this.rynkowa, 1000.0) / 1000.0));
         double low = this.naReke / (1.0 + this.profit);
         this.progLow = this.rynkowa < 100.0 ? Math.floor(low * 100.0) / 100.0 : Math.floor(low);
         this.progOver = this.rynkowa * 1.1;
         double sumaMid = 0.0;
         double zysk = -1.0;

         for (double c : p) {
            int poz = this.poziom(c);
            this.ile[poz]++;
            if (poz == 1) {
               sumaMid += c;
            }

            if (poz == 0) {
               zysk = Math.max(zysk, this.zysk(c));
            }
         }

         this.sredniaMid = this.ile[1] == 0 ? this.rynkowa : sumaMid / this.ile[1];
         this.najlepszyZysk = zysk;
      }
   }

   public static double podloga(double[] p) {
      int n = p.length;
      int wsparcie = n >= 10 ? 3 : (n >= 4 ? 2 : 1);

      for (int i = 0; i < n; i++) {
         int ile = 0;

         for (int j = i; j < n && p[j] <= p[i] * 1.1; j++) {
            ile++;
         }

         if (ile >= wsparcie) {
            return p[i];
         }
      }

      return p[n / 2];
   }

   public boolean jestLow() {
      return this.progLow > 0.0;
   }

   public int poziom(double cena) {
      if (cena <= 0.0 || this.rynkowa <= 0.0) {
         return 1;
      } else if (this.jestLow() && cena <= this.progLow) {
         return 0;
      } else {
         return cena > this.progOver ? 2 : 1;
      }
   }

   public double zysk(double cena) {
      return this.naReke - cena;
   }
}
