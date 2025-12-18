import java.util.*;

// ===============================
// Custom Exception
// ===============================
class KuantumCokusuException extends Exception {
    public KuantumCokusuException(String id) {
        super("KUANTUM ÇÖKÜŞÜ! Nesne patladı: " + id);
    }
}

// ===============================
// Interface
// ===============================
interface IKritik {
    void acilDurumSogutmasi();
}

// ===============================
// Abstract Class
// ===============================
abstract class KuantumNesnesi {
    private String id;
    private double stabilite;
    private int tehlikeSeviyesi;

    public KuantumNesnesi(String id, double stabilite, int tehlikeSeviyesi) {
        this.id = id;
        setStabilite(stabilite);
        setTehlikeSeviyesi(tehlikeSeviyesi);
    }

    public String getId() {
        return id;
    }

    public double getStabilite() {
        return stabilite;
    }

    public void setStabilite(double stabilite) {
        if (stabilite < 0) this.stabilite = 0;
        else if (stabilite > 100) this.stabilite = 100;
        else this.stabilite = stabilite;
    }

    public int getTehlikeSeviyesi() {
        return tehlikeSeviyesi;
    }

    public void setTehlikeSeviyesi(int tehlikeSeviyesi) {
        if (tehlikeSeviyesi < 1 || tehlikeSeviyesi > 10) {
            throw new IllegalArgumentException("Tehlike seviyesi 1-10 arası olmalıdır!");
        }
        this.tehlikeSeviyesi = tehlikeSeviyesi;
    }

    protected void stabiliteKontrol() throws KuantumCokusuException {
        if (stabilite <= 0) {
            throw new KuantumCokusuException(id);
        }
    }

    public String durumBilgisi() {
        return "ID: " + id + " | Stabilite: " + stabilite;
    }

    public abstract void analizEt() throws KuantumCokusuException;
}

// ===============================
// Concrete Classes
// ===============================

// VeriPaketi (IKritik DEĞİL)
class VeriPaketi extends KuantumNesnesi {

    public VeriPaketi(String id, double stabilite, int tehlike) {
        super(id, stabilite, tehlike);
    }

    @Override
    public void analizEt() throws KuantumCokusuException {
        System.out.println("Veri içeriği okundu.");
        setStabilite(getStabilite() - 5);
        stabiliteKontrol();
    }
}

// KaranlikMadde (IKritik)
class KaranlikMadde extends KuantumNesnesi implements IKritik {

    public KaranlikMadde(String id, double stabilite, int tehlike) {
        super(id, stabilite, tehlike);
    }

    @Override
    public void analizEt() throws KuantumCokusuException {
        setStabilite(getStabilite() - 15);
        stabiliteKontrol();
    }

    @Override
    public void acilDurumSogutmasi() {
        setStabilite(getStabilite() + 50);
        System.out.println("Acil soğutma uygulandı.");
    }
}

// AntiMadde (En Tehlikeli)
class AntiMadde extends KuantumNesnesi implements IKritik {

    public AntiMadde(String id, double stabilite, int tehlike) {
        super(id, stabilite, tehlike);
    }

    @Override
    public void analizEt() throws KuantumCokusuException {
        System.out.println("EVRENİN DOKUSU TİTRİYOR...");
        setStabilite(getStabilite() - 25);
        stabiliteKontrol();
    }

    @Override
    public void acilDurumSogutmasi() {
        setStabilite(getStabilite() + 50);
        System.out.println("Acil soğutma uygulandı.");
    }
}

// ===============================
// MAIN CLASS
// ===============================
public class Main {

    static Scanner scanner = new Scanner(System.in);
    static List<KuantumNesnesi> envanter = new ArrayList<>();
    static Random rnd = new Random();

    public static void main(String[] args) {

        while (true) {
            try {
                menu();
                int secim = Integer.parseInt(scanner.nextLine());

                switch (secim) {
                    case 1 -> nesneEkle();
                    case 2 -> listele();
                    case 3 -> analizEt();
                    case 4 -> sogut();
                    case 5 -> {
                        System.out.println("Çıkış yapılıyor...");
                        return;
                    }
                    default -> System.out.println("Geçersiz seçim!");
                }

            } catch (KuantumCokusuException e) {
                System.out.println("\nSİSTEM ÇÖKTÜ! TAHLİYE BAŞLATILIYOR...");
                System.out.println(e.getMessage());
                return;
            } catch (Exception e) {
                System.out.println("Hata: " + e.getMessage());
            }
        }
    }

    // ===============================
    // Menu & Actions
    // ===============================
    static void menu() {
        System.out.println("""
                
                KUANTUM AMBARI KONTROL PANELİ
                1. Yeni Nesne Ekle
                2. Tüm Envanteri Listele
                3. Nesneyi Analiz Et
                4. Acil Durum Soğutması Yap
                5. Çıkış
                Seçiminiz:
                """);
    }

    static void nesneEkle() {
        int tip = rnd.nextInt(3);
        String id = "QN-" + rnd.nextInt(1000);
        double stabilite = 60 + rnd.nextInt(41);
        int tehlike = 1 + rnd.nextInt(10);

        KuantumNesnesi nesne;

        if (tip == 0)
            nesne = new VeriPaketi(id, stabilite, tehlike);
        else if (tip == 1)
            nesne = new KaranlikMadde(id, stabilite, tehlike);
        else
            nesne = new AntiMadde(id, stabilite, tehlike);

        envanter.add(nesne);
        System.out.println("Yeni nesne eklendi: " + nesne.durumBilgisi());
    }

    static void listele() {
        if (envanter.isEmpty()) {
            System.out.println("Envanter boş.");
            return;
        }
        for (KuantumNesnesi n : envanter) {
            System.out.println(n.durumBilgisi());
        }
    }

    static void analizEt() throws KuantumCokusuException {
        System.out.print("ID giriniz: ");
        String id = scanner.nextLine();

        for (KuantumNesnesi n : envanter) {
            if (n.getId().equals(id)) {
                n.analizEt();
                System.out.println(n.durumBilgisi());
                return;
            }
        }
        System.out.println("Nesne bulunamadı!");
    }

    static void sogut() {
        System.out.print("ID giriniz: ");
        String id = scanner.nextLine();

        for (KuantumNesnesi n : envanter) {
            if (n.getId().equals(id)) {
                if (n instanceof IKritik kritik) {
                    kritik.acilDurumSogutmasi();
                } else {
                    System.out.println("Bu nesne soğutulamaz!");
                }
                return;
            }
        }
        System.out.println("Nesne bulunamadı!");
    }
}
