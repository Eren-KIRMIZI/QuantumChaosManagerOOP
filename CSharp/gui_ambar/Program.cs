using System;
using System.Collections.Generic;
using System.Drawing;
using System.Windows.Forms;

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

    double stabilite;
    public double Stabilite
    {
        get => stabilite;
        protected set => stabilite = Math.Clamp(value, 0, 100);
    }

    int tehlike;
    public int TehlikeSeviyesi
    {
        get => tehlike;
        protected set
        {
            if (value < 1 || value > 10)
                throw new ArgumentException("Tehlike seviyesi 1-10 arası olmalı!");
            tehlike = value;
        }
    }

    protected KuantumNesnesi(string id, double s, int t)
    {
        ID = id;
        Stabilite = s;
        TehlikeSeviyesi = t;
    }

    protected void StabiliteKontrol()
    {
        if (Stabilite <= 0)
            throw new KuantumCokusuException(ID);
    }

    public virtual string DurumBilgisi()
    {
        return $"ID:{ID} | Stabilite:{Stabilite:F1}% | Tehlike:{TehlikeSeviyesi} | Tip:{GetType().Name}";
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
        return "Veri paketi analiz edildi.";
    }
}

class KaranlikMadde : KuantumNesnesi, IKritik
{
    public KaranlikMadde(string id, double s, int t) : base(id, s, t) { }
    public override string AnalizEt()
    {
        Stabilite -= 15;
        StabiliteKontrol();
        return "Karanlık madde rezonans altında.";
    }
    public void AcilDurumSogutmasi() => Stabilite += 50;
}

class AntiMadde : KuantumNesnesi, IKritik
{
    public AntiMadde(string id, double s, int t) : base(id, s, t) { }
    public override string AnalizEt()
    {
        Stabilite -= 25;
        StabiliteKontrol();
        return "EVRENSEL TİTREŞİM ALGILANDI!";
    }
    public void AcilDurumSogutmasi() => Stabilite += 50;
}

// ===============================
// FORM
// ===============================
class KuantumForm : Form
{
    List<KuantumNesnesi> envanter = new();
    Random rnd = new();

    ListBox liste;
    TextBox txtID;

    public KuantumForm()
    {
        Text = "KUANTUM AMBARI KONTROL PANELİ";
        Width = 900;
        Height = 500;

        Button btnEkle = new() { Text = "Yeni Nesne", Left = 20, Top = 20, Width = 150 };
        Button btnListe = new() { Text = "Listele", Left = 20, Top = 60, Width = 150 };
        Button btnAnaliz = new() { Text = "Analiz Et", Left = 20, Top = 140, Width = 150 };
        Button btnSogut = new() { Text = "Acil Soğutma", Left = 20, Top = 180, Width = 150 };

        txtID = new() { Left = 20, Top = 110, Width = 150, PlaceholderText = "QN-XXXX" };

        liste = new()
        {
            Left = 200,
            Top = 20,
            Width = 660,
            Height = 400
        };

        btnEkle.Click += (s, e) => YeniNesne();
        btnListe.Click += (s, e) => Listele();
        btnAnaliz.Click += (s, e) => AnalizEt();
        btnSogut.Click += (s, e) => Sogut();

        Controls.AddRange(new Control[] { btnEkle, btnListe, btnAnaliz, btnSogut, txtID, liste });
    }

    void YeniNesne()
    {
        string id = "QN-" + rnd.Next(1000, 9999);
        double s = rnd.Next(60, 101);
        int t = rnd.Next(1, 11);

        KuantumNesnesi n = rnd.Next(3) switch
        {
            0 => new VeriPaketi(id, s, t),
            1 => new KaranlikMadde(id, s, t),
            _ => new AntiMadde(id, s, t)
        };

        envanter.Add(n);
        liste.Items.Add("EKLENDİ ? " + n.DurumBilgisi());
    }

    void Listele()
    {
        liste.Items.Clear();
        foreach (var n in envanter)
            liste.Items.Add(n.DurumBilgisi());
    }

    void AnalizEt()
    {
        try
        {
            var n = envanter.Find(x => x.ID == txtID.Text);
            if (n == null) return;

            liste.Items.Add(n.AnalizEt());
            liste.Items.Add(n.DurumBilgisi());
        }
        catch (KuantumCokusuException ex)
        {
            MessageBox.Show(ex.Message, "SİSTEM ÇÖKTÜ", MessageBoxButtons.OK, MessageBoxIcon.Error);
            Application.Exit();
        }
    }

    void Sogut()
    {
        var n = envanter.Find(x => x.ID == txtID.Text);
        if (n is IKritik k)
        {
            k.AcilDurumSogutmasi();
            liste.Items.Add("SOĞUTMA UYGULANDI ? " + n.ID);
        }
    }
}

// ===============================
// PROGRAM
// ===============================
static class Program
{
    [STAThread]
    static void Main()
    {
        Application.EnableVisualStyles();
        Application.Run(new KuantumForm());
    }
}
