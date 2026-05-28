import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import org.aruhaz.*;

class AruhazCR0Tesztek {
    private double kerekites5re(double osszeg) {
        double maradek = osszeg % 10.0;
        if (maradek < 2.5) {
            return osszeg - maradek;
        } else if (maradek < 5.0) {
            return osszeg - maradek + 5.0;
        } else if (maradek < 7.5) {
            return osszeg - maradek + 5.0;
        } else {
            return osszeg - maradek + 10.0;
        }
    }
    @Test
    void teszt_cr0_pelda1_1kgAlma() {
        double egysegAr = 500.0;
        double mennyiseg = 1.0;
        Aruhaz target = new Aruhaz(Termek.ALMA, egysegAr);
        double expected = kerekites5re(egysegAr * mennyiseg);
        Kosar kosar = new Kosar(List.of(new Tetel(Termek.ALMA, mennyiseg)));
        double actual = target.getKosarAr(kosar);
        assertEquals(expected, actual, 0.001);
    }
    @Test
    void teszt_cr0_pelda2_2kgAlma() {
        double egysegAr = 500.0;
        double mennyiseg = 2.0;
        Aruhaz target = new Aruhaz(Termek.ALMA, egysegAr);
        double expected = kerekites5re(egysegAr * mennyiseg);
        Kosar kosar = new Kosar(List.of(new Tetel(Termek.ALMA, mennyiseg)));
        double actual = target.getKosarAr(kosar);
        assertEquals(expected, actual, 0.001);
    }
    @Test
    void teszt_cr0_pelda3_5kgAlma_kedvezmennyel() {
        double egysegAr = 500.0;
        double hatar1 = 5.0;
        double kedvezmeny1 = 0.1;
        double mennyiseg = hatar1;
        Aruhaz target = new Aruhaz(Termek.ALMA, egysegAr);
        target.setKedvezmeny(Termek.ALMA, hatar1, kedvezmeny1);
        double expected = kerekites5re(
                egysegAr * mennyiseg * (1.0-kedvezmeny1));
        Kosar kosar = new Kosar(List.of(new Tetel(Termek.ALMA, mennyiseg)));
        double actual = target.getKosarAr(kosar);
        assertEquals(expected, actual, 0.001);
    }
    @Test
    void teszt_cr0_pelda4_20kgAlma_kedvezmennyel() {
        double egysegAr = 500.0;
        double hatar1 = 5.0;
        double kedvezmeny1 = 0.1;
        double hatar2 = 20.0;
        double kedvezmeny2 = 0.15;
        double mennyiseg = hatar2;
        Aruhaz target = new Aruhaz(Termek.ALMA, egysegAr);
        target.setKedvezmeny(Termek.ALMA, hatar1, kedvezmeny1);
        target.setKedvezmeny(Termek.ALMA, hatar2, kedvezmeny2);
        double expected = kerekites5re(
                egysegAr * mennyiseg * (1.0-kedvezmeny2));
        Kosar kosar = new Kosar(List.of(new Tetel(Termek.ALMA, mennyiseg)));
        double actual = target.getKosarAr(kosar);
        assertEquals(expected, actual, 0.001);
    }
    @Test
    void teszt_cr0_pelda5_1kgAlma_1kgBanan() {
        double egysegArAlma = 500.0;
        double mennyisegAlma = 1.0;
        double egysegArBanan = 450.0;
        double mennyisegBanan = 1.0;
        Aruhaz target = new Aruhaz(List.of(
                new TermekAr(Termek.ALMA, egysegArAlma),
                new TermekAr(Termek.BANAN, egysegArBanan)
        ));
        Kosar kosar = new Kosar(List.of(
                new Tetel(Termek.ALMA, mennyisegAlma),
                new Tetel(Termek.BANAN, mennyisegBanan)
        ));
        double expected = kerekites5re(egysegArAlma*mennyisegAlma +
                egysegArBanan*mennyisegBanan);
        double actual = target.getKosarAr(kosar);
        assertEquals(expected, actual, 0.001);
    }
    @Test
    void teszt_cr0_pelda6_2kgBanan_kedvezmennyel() {
        double egysegAr = 450.0;
        double hatar1 = 2.0;
        double kedvezmeny1 = 0.1;
        double mennyiseg = hatar1;
        Aruhaz target = new Aruhaz(Termek.BANAN, egysegAr);
        target.setKedvezmeny(Termek.BANAN, hatar1, kedvezmeny1);
        double expected = kerekites5re(
                egysegAr * mennyiseg * (1.0-kedvezmeny1));
        Kosar kosar = new Kosar(List.of(new Tetel(Termek.BANAN, mennyiseg)));
        double actual = target.getKosarAr(kosar);
        assertEquals(expected, actual, 0.001);
    }
    @Test
    void teszt_cr0_pelda7_tobbAlma_osszesen4kg() {
        double egysegAr = 500.0;
        double mennyiseg1 = 1.0;
        double mennyiseg2 = 2.0;
        double mennyiseg3 = 2.0;
        double osszesMennyiseg = mennyiseg1 + mennyiseg2 + mennyiseg3;
        Aruhaz target = new Aruhaz(Termek.ALMA, egysegAr);
        double hatar1 = 5.0;
        double kedvezmeny1 = 0.1;
        target.setKedvezmeny(Termek.ALMA, hatar1, kedvezmeny1);
        Kosar kosar = new Kosar(List.of(
                new Tetel(Termek.ALMA, mennyiseg1),
                new Tetel(Termek.ALMA, mennyiseg2),
                new Tetel(Termek.ALMA, mennyiseg3)
        ));
        double expected = kerekites5re(
                egysegAr * osszesMennyiseg * (1.0-kedvezmeny1));
        double actual = target.getKosarAr(kosar);
        assertEquals(expected, actual, 0.001);
    }
    @Test
    void teszt_cr0_pelda8_2kgAlma_2kgBanan() {
        double egysegArAlma = 500.0;
        double egysegArBanan = 450.0;
        double kedvezmenyHatarBanan = 2.0;
        double kedvezmenyBanan = 0.1;
        double mennyisegAlma = 2.0;
        double mennyisegBanan = 2.0;
        Aruhaz target = new Aruhaz(List.of(
                new TermekAr(Termek.ALMA, egysegArAlma),
                new TermekAr(Termek.BANAN, egysegArBanan)
        ));
        target.setKedvezmeny(Termek.BANAN, kedvezmenyHatarBanan, kedvezmenyBanan);
        double bruttoAlma = egysegArAlma * mennyisegAlma;
        double bruttoBanan = egysegArBanan * mennyisegBanan;
        double nettoBanan = bruttoBanan * (1.0 - kedvezmenyBanan);
        double expected = kerekites5re(bruttoAlma + nettoBanan);
        Kosar kosar = new Kosar(List.of(
                new Tetel(Termek.ALMA, mennyisegAlma),
                new Tetel(Termek.BANAN, mennyisegBanan)
        ));
        double actual = target.getKosarAr(kosar);
        assertEquals(expected, actual, 0.001);
    }
    @Test
    void teszt_cr0_pelda9_1_01kgAlma() {
        double egysegAr = 500.0;
        double mennyiseg = 1.01;
        Aruhaz target = new Aruhaz(Termek.ALMA, egysegAr);
        double expected = kerekites5re(egysegAr * mennyiseg);
        Kosar kosar = new Kosar(List.of(new Tetel(Termek.ALMA, mennyiseg)));
        double actual = target.getKosarAr(kosar);
        assertEquals(expected, actual, 0.001);
    }
    @Test
    void teszt_cr0_pelda10_1_99kgBanan() {
        double egysegAr = 450.0;
        double mennyiseg = 1.99;
        Aruhaz target = new Aruhaz(Termek.BANAN, egysegAr);
        double brutto = egysegAr * mennyiseg;
        double expected = kerekites5re(brutto);
        Kosar kosar = new Kosar(List.of(new Tetel(Termek.BANAN, mennyiseg)));
        double actual = target.getKosarAr(kosar);
        assertEquals(expected, actual, 0.001);
    }
    @Test
    void teszt_cr0_pelda11_1_789kgAlma() {
        double egysegAr = 500.0;
        double mennyiseg = 1.789;
        Aruhaz target = new Aruhaz(Termek.ALMA, egysegAr);
        double brutto = egysegAr * mennyiseg;
        double expected = kerekites5re(brutto);
        Kosar kosar = new Kosar(List.of(new Tetel(Termek.ALMA, mennyiseg)));
        double actual = target.getKosarAr(kosar);
        assertEquals(expected, actual, 0.001);
    }
    @Test
    void teszt_cr0_pelda12_2_01kgBanan_kedvezmennyel() {
        double egysegAr = 450.0;
        double mennyiseg = 2.01;
        double kedvezmenyHatar = 2.0;
        double kedvezmeny = 0.1;
        Aruhaz target = new Aruhaz(Termek.BANAN, egysegAr);
        target.setKedvezmeny(Termek.BANAN, kedvezmenyHatar, kedvezmeny);
        double brutto = egysegAr * mennyiseg;
        double netto = brutto * (1.0 - kedvezmeny);
        System.out.println(netto);
        double expected = kerekites5re(netto);
        System.out.println(expected);
        Kosar kosar = new Kosar(List.of(new Tetel(Termek.BANAN, mennyiseg)));
        double actual = target.getKosarAr(kosar);
        assertEquals(expected, actual, 0.001);
    }
    @Test
    void teszt_cr0_pelda13_3_333kgAlma_1_777kgBanan() {
        double egysegArAlma = 500.0;
        double egysegArBanan = 450.0;
        double mennyisegAlma = 3.333;
        double mennyisegBanan = 1.777;
        Aruhaz target = new Aruhaz(List.of(
                new TermekAr(Termek.ALMA, egysegArAlma),
                new TermekAr(Termek.BANAN, egysegArBanan)
        ));
        double bruttoAlma = egysegArAlma * mennyisegAlma;
        double bruttoBanan = egysegArBanan * mennyisegBanan;
        double brutto = bruttoAlma + bruttoBanan;
        double expected = kerekites5re(brutto);
        Kosar kosar = new Kosar(List.of(
                new Tetel(Termek.ALMA, mennyisegAlma),
                new Tetel(Termek.BANAN, mennyisegBanan)
        ));
        double actual = target.getKosarAr(kosar);
        assertEquals(expected, actual, 0.001);
    }
    @Test
    void teszt_cr0_pelda14_5_001kgAlma_kedvezmennyel() {
        double egysegAr = 500.0;
        double mennyiseg = 5.001;
        double kedvezmenyHatar = 5.0;
        double kedvezmeny = 0.1;
        Aruhaz target = new Aruhaz(Termek.ALMA, egysegAr);
        target.setKedvezmeny(Termek.ALMA, kedvezmenyHatar, kedvezmeny);
        double brutto = egysegAr * mennyiseg;
        double netto = brutto * (1.0 - kedvezmeny);
        double expected = kerekites5re(netto);
        Kosar kosar = new Kosar(List.of(new Tetel(Termek.ALMA, mennyiseg)));
        double actual = target.getKosarAr(kosar);
        assertEquals(expected, actual, 0.001);
    }
    @Test
    void teszt_cr0_pelda15_3_789kgAlma_2_777kgBanan() {
        double egysegArAlma = 500.0;
        double egysegArBanan = 450.0;
        double mennyisegAlma = 3.789;
        double mennyisegBanan = 2.777;
        double kedvezmenyHatarBanan = 2.0;
        double kedvezmenyBanan = 0.1;
        Aruhaz target = new Aruhaz(List.of(
                new TermekAr(Termek.ALMA, egysegArAlma),
                new TermekAr(Termek.BANAN, egysegArBanan)
        ));
        target.setKedvezmeny(Termek.BANAN, kedvezmenyHatarBanan, kedvezmenyBanan);
        double bruttoAlma = egysegArAlma * mennyisegAlma;
        double bruttoBanan = egysegArBanan * mennyisegBanan;
        double nettoBanan = bruttoBanan * (1.0 - kedvezmenyBanan);
        double osszesen = bruttoAlma + nettoBanan;
        double expected = kerekites5re(osszesen);
        Kosar kosar = new Kosar(List.of(
                new Tetel(Termek.ALMA, mennyisegAlma),
                new Tetel(Termek.BANAN, mennyisegBanan)
        ));
        double actual = target.getKosarAr(kosar);
        assertEquals(expected, actual, 0.001);
    }
}
