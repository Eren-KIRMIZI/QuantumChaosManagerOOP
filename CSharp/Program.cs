using System;
using System.Collections.Generic;

// ===============================
// Custom Exception
// ===============================
class KuantumCokusuException : Exception
{
    public string NesneId { get; }

    public KuantumCokusuException(string id)
        : base($"KUANTUM ÇÖKÜŞÜ! Nesne patladı: {id}")
    {
        NesneId = id;
    }
}

// ===============================
// Interface
// ===============================
interface IKritik
{
    void AcilDurumSogutmasi();
}

// ===============================
// Abstract Class
// ===============================
abstract class KuantumNesnesi
{
    public string ID { get; private set; }

    private double stabilite;
    public double Stabilite
    {
        get => stabilite;
        protected set
        {
            if (value < 0) stabilite = 0;
            else if (value > 100) stabilite = 100;
            else stabilite = value;
        }
    }

    private int tehlikeSeviyesi;
    public int TehlikeSeviyesi
    {
        get => tehlikeSeviyesi;
        protected set
        {
            if (value < 1 || value > 10)
                throw new ArgumentException("Tehlike seviyesi 1-10 arası olmalı!");
            tehlikeSeviyesi = value;
        }
    }

    protected KuantumNesnesi(string id, double stabilite, int tehlike)
    {
        ID = id;
        Stabilite = stabilite;
        TehlikeSeviyesi = tehlike;
    }

    protected void StabiliteKontrol()
    {
        if (Stabilite <= 0)
            throw new KuantumCokusuException(ID);
    }

    public virtual string DurumBilgisi()
    {
        return $"ID: {ID} | Stabilite: {Stabilite:F1}% | Tehlike: {TehlikeSeviyesi} | Tip: {GetType().Name}";
    }

    public abstract string AnalizEt();
}

// ===============================
// Concrete Classes
// ===============================
class VeriPaketi : KuantumNesnesi
{
    public VeriPaketi(string id, double s, int t) : base(id, s, t) { }

    public override string AnalizEt()
    {
        Stabilite -= 5;
        StabiliteKontrol();
        return "Veri içeriği okundu.";
    }
}

class KaranlikMadde : KuantumNesnesi, IKritik
{
    public KaranlikMadde(string id, double s, int t) : base(id, s, t) { }

    public override string AnalizEt()
    {
        Stabilite -= 15;
        StabiliteKontrol();
        return "Karanlık madde analiz ediliyor...";
    }

    public void AcilDurumSogutmasi()
    {
        Stabilite += 50;
    }
}

class AntiMadde : KuantumNesnesi, IKritik
{
    public AntiMadde(string id, double s, int t) : base(id, s, t) { }

    public override string AnalizEt()
    {
        Stabilite -= 25;
        StabiliteKontrol();
        return "EVRENİN DOKUSU TİTRİYOR...";
    }

    public void AcilDurumSogutmasi()
    {
        Stabilite += 50;
    }
}

// ===============================
// MAIN PROGRAM
// ===============================
class Program
{
    static List<KuantumNesnesi> envanter = new();
    static Random rnd = new();

    static void Main()
    {
        Console.ForegroundColor = ConsoleColor.Green;
        Console.WriteLine("KUANTUM AMBARI KONTROL PANELİ\n");

        try
        {
            while (true)
            {
                Menu();
                Console.Write("Seçiminiz: ");
                string secim = Console.ReadLine();

                switch (secim)
                {
                    case "1": YeniNesneEkle(); break;
                    case "2": Listele(); break;
                    case "3": AnalizEt(); break;
                    case "4": Sogutma(); break;
                    case "5": return;
                    default: Console.WriteLine("Geçersiz seçim!"); break;
                }
            }
        }
        catch (KuantumCokusuException ex)
        {
            Console.ForegroundColor = ConsoleColor.Red;
            Console.WriteLine("\nSİSTEM ÇÖKTÜ!");
            Console.WriteLine("TAHLİYE BAŞLATILIYOR...");
            Console.WriteLine(ex.Message);
        }
    }

    static void Menu()
    {
        Console.WriteLine("\n1. Yeni Nesne Ekle");
        Console.WriteLine("2. Tüm Envanteri Listele");
        Console.WriteLine("3. Nesneyi Analiz Et");
        Console.WriteLine("4. Acil Durum Soğutması Yap");
        Console.WriteLine("5. Çıkış");
    }

    static void YeniNesneEkle()
    {
        string id = "QN-" + rnd.Next(1000, 9999);
        double stabilite = rnd.Next(60, 101);
        int tehlike = rnd.Next(1, 11);

        int tip = rnd.Next(3);
        KuantumNesnesi nesne = tip switch
        {
            0 => new VeriPaketi(id, stabilite, tehlike),
            1 => new KaranlikMadde(id, stabilite, tehlike),
            _ => new AntiMadde(id, stabilite, tehlike)
        };

        envanter.Add(nesne);
        Console.WriteLine($"Yeni nesne eklendi: {nesne.DurumBilgisi()}");
    }

    static void Listele()
    {
        if (envanter.Count == 0)
        {
            Console.WriteLine("Envanter boş.");
            return;
        }

        foreach (var n in envanter)
            Console.WriteLine(n.DurumBilgisi());
    }

    static void AnalizEt()
    {
        Console.Write("Nesne ID: ");
        string id = Console.ReadLine();

        var nesne = envanter.Find(n => n.ID == id);
        if (nesne == null)
        {
            Console.WriteLine("Nesne bulunamadı!");
            return;
        }

        Console.WriteLine(nesne.AnalizEt());
        Console.WriteLine(nesne.DurumBilgisi());
    }

    static void Sogutma()
    {
        Console.Write("Nesne ID: ");
        string id = Console.ReadLine();

        var nesne = envanter.Find(n => n.ID == id);
        if (nesne == null)
        {
            Console.WriteLine("Nesne bulunamadı!");
            return;
        }

        if (nesne is IKritik kritik)
        {
            kritik.AcilDurumSogutmasi();
            Console.WriteLine("Acil soğutma uygulandı!");
        }
        else
        {
            Console.WriteLine("Bu nesne soğutulamaz!");
        }
    }
}
