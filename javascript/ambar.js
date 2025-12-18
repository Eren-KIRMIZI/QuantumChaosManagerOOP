// ===============================
// Custom Exception
// ===============================
class KuantumCokusuException extends Error {
    constructor(id) {
        super(`KUANTUM ÇÖKÜŞÜ! Nesne patladı: ${id}`);
        this.name = "KuantumCokusuException";
        this.nesneId = id;
    }
}

// ===============================
// Abstract Class (KuantumNesnesi)
// ===============================
class KuantumNesnesi {
    constructor(id, stabilite, tehlikeSeviyesi) {
        // Soyut sınıf kontrolü
        if (new.target === KuantumNesnesi) {
            throw new Error("KuantumNesnesi soyut sınıftır, direkt örnek oluşturulamaz!");
        }
        this._id = id;
        this._stabilite = 0;
        this._tehlikeSeviyesi = 0;
        
        // Setter'lar üzerinden atama (encapsulation)
        this.stabilite = stabilite;
        this.tehlikeSeviyesi = tehlikeSeviyesi;
    }

    // ID getter (sadece okunabilir)
    get id() {
        return this._id;
    }

    // Stabilite getter/setter (Encapsulation: 0-100 arası kısıtlama)
    get stabilite() {
        return this._stabilite;
    }

    set stabilite(value) {
        if (typeof value !== 'number' || isNaN(value)) {
            throw new Error("Stabilite sayısal değer olmalıdır!");
        }
        // 0-100 arası kısıtlama
        if (value < 0) {
            this._stabilite = 0;
        } else if (value > 100) {
            this._stabilite = 100;
        } else {
            this._stabilite = value;
        }
    }

    // TehlikeSeviyesi getter/setter (Encapsulation: 1-10 arası)
    get tehlikeSeviyesi() {
        return this._tehlikeSeviyesi;
    }

    set tehlikeSeviyesi(value) {
        if (typeof value !== 'number' || value < 1 || value > 10) {
            throw new Error("TehlikeSeviyesi 1-10 arası olmalıdır!");
        }
        this._tehlikeSeviyesi = Math.floor(value);
    }

    // Abstract metot (override edilmeli)
    analizEt() {
        throw new Error("analizEt() metodu alt sınıflarda override edilmelidir!");
    }

    // Durum bilgisi
    durumBilgisi() {
        return `ID: ${this.id} | Stabilite: ${this.stabilite.toFixed(1)} | Tehlike: ${this.tehlikeSeviyesi} | Tip: ${this.constructor.name}`;
    }

    // Stabilite kontrolü (her analiz sonrası)
    _stabiliteKontrol() {
        if (this.stabilite <= 0) {
            throw new KuantumCokusuException(this.id);
        }
    }
}

// ===============================
// Interface (IKritik) - Mixin Pattern
// ===============================
const IKritik = Base => class extends Base {
    acilDurumSogutmasi() {
        const eskiStabilite = this.stabilite;
        this.stabilite += 50; // Setter otomatik max 100 yapacak
        console.log(`[SOĞUTMA] ${this.id}: ${eskiStabilite.toFixed(1)} -> ${this.stabilite.toFixed(1)}`);
    }
    
    // IKritik interface'ini implement ettiğini belirtmek için
    get isKritik() {
        return true;
    }
};

// ===============================
// Concrete Classes (Kalıtım & Polimorfizm)
// ===============================

// VeriPaketi - IKritik DEĞİL
class VeriPaketi extends KuantumNesnesi {
    analizEt() {
        console.log("Veri içeriği okundu.");
        this.stabilite -= 5;
        this._stabiliteKontrol();
    }
    
    get isKritik() {
        return false;
    }
}

// KaranlikMadde - IKritik
class KaranlikMadde extends IKritik(KuantumNesnesi) {
    analizEt() {
        console.log("Karanlık madde analiz ediliyor...");
        this.stabilite -= 15;
        this._stabiliteKontrol();
    }
}

// AntiMadde - IKritik (En tehlikeli)
class AntiMadde extends IKritik(KuantumNesnesi) {
    analizEt() {
        console.log("EVRENİN DOKUSU TİTRİYOR...");
        this.stabilite -= 25;
        this._stabiliteKontrol();
    }
}

// ===============================
// Utility Functions
// ===============================
function rastgeleID() {
    return "QN-" + Math.random().toString(36).substring(2, 8).toUpperCase();
}

function rastgeleNesneUret() {
    const tipler = [VeriPaketi, KaranlikMadde, AntiMadde];
    const TipClass = tipler[Math.floor(Math.random() * tipler.length)];
    
    const stabilite = Math.floor(Math.random() * 40) + 60; // 60-100 arası
    const tehlike = Math.floor(Math.random() * 10) + 1;    // 1-10 arası
    
    return new TipClass(rastgeleID(), stabilite, tehlike);
}

// ===============================
// Main Application
// ===============================
const readline = require("readline");
const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

let envanter = [];

function menu() {
    console.log(`
${"=".repeat(50)}
   KUANTUM AMBARI KONTROL PANELİ  
${"=".repeat(50)}

1. Yeni Nesne Ekle (Rastgele)
2. Tüm Envanteri Listele
3. Nesneyi Analiz Et (ID)
4. Acil Durum Soğutması Yap (IKritik)
5. Çıkış

Envanter: ${envanter.length} nesne
${"=".repeat(50)}
`);
}

// Her işlemi try-catch ile sarmalama fonksiyonu
function guvenliIslem(callback) {
    try {
        callback();
    } catch (e) {
        if (e instanceof KuantumCokusuException) {
            console.log("\n" + "!".repeat(50));
            console.log("SİSTEM ÇÖKTÜ! TAHLİYE BAŞLATILIYOR...");
            console.log("!" + e.message);
            console.log("!".repeat(50) + "\n");
            rl.close();
            process.exit(1);
        } else {
            console.error("Hata:", e.message);
        }
    }
}

function mainLoop() {
    menu();
    rl.question("Seçiminiz: ", secim => {
        secim = secim.trim();
        
        switch (secim) {
            case "1":
                guvenliIslem(() => {
                    const nesne = rastgeleNesneUret();
                    envanter.push(nesne);
                    const simge = nesne.isKritik ? "False" : "True";
                    console.log(`\n${simge} Yeni nesne eklendi:`);
                    console.log(`   ${nesne.durumBilgisi()}\n`);
                });
                mainLoop();
                break;

            case "2":
                if (envanter.length === 0) {
                    console.log("\nEnvanter boş.\n");
                } else {
                    console.log("\nENVANTER RAPORU:");
                    console.log("-".repeat(80));
                    envanter.forEach((n, idx) => {
                        const kritikBadge = n.isKritik ? " [KRİTİK]" : "";
                        const stabiliteBar = "".repeat(Math.floor(n.stabilite / 10)) + 
                                           "".repeat(10 - Math.floor(n.stabilite / 10));
                        console.log(`${idx + 1}. ${n.durumBilgisi()}${kritikBadge}`);
                        console.log(`   Stabilite: [${stabiliteBar}]`);
                    });
                    console.log("-".repeat(80) + "\n");
                }
                mainLoop();
                break;

            case "3":
                rl.question("Analiz edilecek ID: ", id => {
                    id = id.trim();
                    guvenliIslem(() => {
                        const nesne = envanter.find(x => x.id === id);
                        if (!nesne) {
                            console.log(`\nNesne bulunamadı: ${id}\n`);
                        } else {
                            console.log(`\nAnaliz başlatılıyor: ${id}`);
                            nesne.analizEt(); // Burada KuantumCokusuException fırlatılabilir
                            console.log(`   ${nesne.durumBilgisi()}`);
                            
                            if (nesne.stabilite < 30) {
                                console.log(`UYARI: Kritik stabilite seviyesi!\n`);
                            } else {
                                console.log();
                            }
                        }
                    });
                    mainLoop();
                });
                return;

            case "4":
                rl.question("Soğutulacak ID: ", id => {
                    id = id.trim();
                    guvenliIslem(() => {
                        const nesne = envanter.find(x => x.id === id);
                        if (!nesne) {
                            console.log(`\nNesne bulunamadı: ${id}\n`);
                        } else if (nesne.isKritik && typeof nesne.acilDurumSogutmasi === "function") {
                            console.log(`\nAcil soğutma uygulanıyor: ${id}`);
                            nesne.acilDurumSogutmasi();
                            console.log(`   ${nesne.durumBilgisi()}\n`);
                        } else {
                            console.log(`\nBu nesne soğutulamaz! (IKritik değil)\n`);
                        }
                    });
                    mainLoop();
                });
                return;

            case "5":
                console.log("\nGüvenli çıkış yapılıyor...\n");
                rl.close();
                return;

            default:
                console.log("\nGeçersiz seçim!\n");
                mainLoop();
        }
    });
}

// Program başlangıcı
console.log("\nKuantum Ambarı sistemi başlatılıyor...\n");
mainLoop();
