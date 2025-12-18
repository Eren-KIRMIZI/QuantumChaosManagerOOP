from __future__ import annotations
from abc import ABC, abstractmethod
from typing import List
import random
import tkinter as tk
from tkinter import ttk, messagebox, scrolledtext

class KuantumCokusuException(Exception):
    def __init__(self, nesne_id: str):
        super().__init__(f"Kuantum Çöküşü! Patlayan nesne ID: {nesne_id}")
        self.nesne_id = nesne_id

class IKritik(ABC):
    @abstractmethod
    def acil_durum_sogutmasi(self) -> None:
        pass

class KuantumNesnesi(ABC):
    def __init__(self, id_: str, stabilite: float, tehlike_seviyesi: int):
        self._id = id_
        self._stabilite = 100.0
        self._tehlike_seviyesi = 1
        self.id = id_
        self.stabilite = stabilite
        self.tehlike_seviyesi = tehlike_seviyesi

    @property
    def id(self) -> str:
        return self._id

    @id.setter
    def id(self, value: str) -> None:
        if not value or not isinstance(value, str):
            raise ValueError("ID geçersiz.")
        self._id = value

    @property
    def stabilite(self) -> float:
        return self._stabilite

    @stabilite.setter
    def stabilite(self, value: float) -> None:
        if value is None:
            raise ValueError("Stabilite boş olamaz.")
        if value > 100:
            self._stabilite = 100.0
        elif value < 0:
            self._stabilite = 0.0
        else:
            self._stabilite = float(value)

    @property
    def tehlike_seviyesi(self) -> int:
        return self._tehlike_seviyesi

    @tehlike_seviyesi.setter
    def tehlike_seviyesi(self, value: int) -> None:
        if not isinstance(value, int) or value < 1 or value > 10:
            raise ValueError("TehlikeSeviyesi 1-10 arası olmalıdır.")
        self._tehlike_seviyesi = value

    @abstractmethod
    def analiz_et(self) -> None:
        pass

    def durum_bilgisi(self) -> str:
        return f"ID: {self.id}, Stabilite: {self.stabilite:.1f}, Tehlike: {self.tehlike_seviyesi}, Tip: {self.__class__.__name__}"

    def _kuantum_cokusu_kontrol(self) -> None:
        if self.stabilite <= 0:
            raise KuantumCokusuException(self.id)

class VeriPaketi(KuantumNesnesi):
    def analiz_et(self) -> None:
        self.stabilite -= 5
        self._kuantum_cokusu_kontrol()
        return "Veri içeriği okundu."

class KaranlikMadde(KuantumNesnesi, IKritik):
    def analiz_et(self) -> None:
        self.stabilite -= 15
        self._kuantum_cokusu_kontrol()
        return "Karanlık madde analiz edildi."

    def acil_durum_sogutmasi(self) -> None:
        self.stabilite = min(100.0, self.stabilite + 50)

class AntiMadde(KuantumNesnesi, IKritik):
    def analiz_et(self) -> None:
        self.stabilite -= 25
        self._kuantum_cokusu_kontrol()
        return "Evrenin dokusu titriyor..."

    def acil_durum_sogutmasi(self) -> None:
        self.stabilite = min(100.0, self.stabilite + 50)

class KuantumAmbarGUI:
    def __init__(self, root):
        self.root = root
        self.root.title("Kuantum Ambarı Kontrol Paneli")
        self.root.geometry("900x700")
        self.root.configure(bg="#1a1a2e")
        
        self.envanter: List[KuantumNesnesi] = []
        self.sayac = 1
        
        self.setup_ui()
        
    def setup_ui(self):
        # Başlık
        title_frame = tk.Frame(self.root, bg="#16213e", pady=15)
        title_frame.pack(fill=tk.X)
        
        title_label = tk.Label(
            title_frame,
            text="KUANTUM AMBARI KONTROL PANELİ",
            font=("Arial", 20, "bold"),
            bg="#16213e",
            fg="#00ff41"
        )
        title_label.pack()
        
        subtitle = tk.Label(
            title_frame,
            text="Omega Sektörü - Vardiya Kontrol Sistemi",
            font=("Arial", 10, "italic"),
            bg="#16213e",
            fg="#00d9ff"
        )
        subtitle.pack()
        
        # Ana container
        main_container = tk.Frame(self.root, bg="#1a1a2e")
        main_container.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)
        
        # Sol panel - Kontroller
        left_panel = tk.Frame(main_container, bg="#16213e", relief=tk.RAISED, bd=2)
        left_panel.pack(side=tk.LEFT, fill=tk.BOTH, padx=5, pady=5)
        
        control_label = tk.Label(
            left_panel,
            text="KONTROL MERKEZİ",
            font=("Arial", 12, "bold"),
            bg="#16213e",
            fg="#ff6b6b"
        )
        control_label.pack(pady=10)
        
        # Butonlar
        btn_style = {
            "font": ("Arial", 10, "bold"),
            "width": 25,
            "height": 2,
            "relief": tk.RAISED,
            "bd": 3
        }
        
        self.btn_ekle = tk.Button(
            left_panel,
            text="Yeni Nesne Ekle",
            command=self.yeni_nesne_ekle,
            bg="#4ecdc4",
            fg="#0f3443",
            **btn_style
        )
        self.btn_ekle.pack(pady=5, padx=10)
        
        self.btn_listele = tk.Button(
            left_panel,
            text="Envanteri Listele",
            command=self.envanteri_listele,
            bg="#95e1d3",
            fg="#0f3443",
            **btn_style
        )
        self.btn_listele.pack(pady=5, padx=10)
        
        # ID girişi frame
        id_frame = tk.Frame(left_panel, bg="#16213e")
        id_frame.pack(pady=10, padx=10, fill=tk.X)
        
        tk.Label(
            id_frame,
            text="Nesne ID:",
            font=("Arial", 10),
            bg="#16213e",
            fg="white"
        ).pack()
        
        self.id_entry = tk.Entry(
            id_frame,
            font=("Arial", 12),
            width=20,
            justify=tk.CENTER,
            bg="#0f3443",
            fg="#00ff41",
            insertbackground="#00ff41"
        )
        self.id_entry.pack(pady=5)
        
        self.btn_analiz = tk.Button(
            left_panel,
            text="Nesneyi Analiz Et",
            command=self.nesne_analiz,
            bg="#f38181",
            fg="#0f3443",
            **btn_style
        )
        self.btn_analiz.pack(pady=5, padx=10)
        
        self.btn_sogutma = tk.Button(
            left_panel,
            text="Acil Durum Soğutması",
            command=self.acil_sogutma,
            bg="#aa96da",
            fg="#0f3443",
            **btn_style
        )
        self.btn_sogutma.pack(pady=5, padx=10)
        
        self.btn_cikis = tk.Button(
            left_panel,
            text="Güvenli Çıkış",
            command=self.guvenli_cikis,
            bg="#ff6b6b",
            fg="white",
            **btn_style
        )
        self.btn_cikis.pack(pady=20, padx=10)
        
        # Sağ panel - Log ve Durum
        right_panel = tk.Frame(main_container, bg="#16213e")
        right_panel.pack(side=tk.RIGHT, fill=tk.BOTH, expand=True, padx=5, pady=5)
        
        # Log başlığı
        log_label = tk.Label(
            right_panel,
            text="SİSTEM LOGU",
            font=("Arial", 12, "bold"),
            bg="#16213e",
            fg="#ffb400"
        )
        log_label.pack(pady=5)
        
        # Log alanı
        self.log_text = scrolledtext.ScrolledText(
            right_panel,
            font=("Courier", 9),
            bg="#0f3443",
            fg="#00ff41",
            insertbackground="#00ff41",
            height=20,
            wrap=tk.WORD
        )
        self.log_text.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)
        
        # Durum çubuğu
        status_frame = tk.Frame(right_panel, bg="#0f3443", relief=tk.SUNKEN, bd=2)
        status_frame.pack(fill=tk.X, pady=5, padx=5)
        
        self.status_label = tk.Label(
            status_frame,
            text="Sistem Aktif | Envanter: 0 nesne",
            font=("Arial", 9),
            bg="#0f3443",
            fg="#00d9ff",
            anchor=tk.W,
            padx=10
        )
        self.status_label.pack(fill=tk.X)
        
        self.log_mesaj("Sistem başlatıldı. Kuantum Ambarı hazır!")
        
    def log_mesaj(self, mesaj: str):
        self.log_text.insert(tk.END, f"{mesaj}\n")
        self.log_text.see(tk.END)
        
    def guncelle_durum(self):
        toplam = len(self.envanter)
        kritik_sayisi = sum(1 for n in self.envanter if isinstance(n, IKritik))
        self.status_label.config(
            text=f"Sistem Aktif | Envanter: {toplam} nesne | Kritik: {kritik_sayisi}"
        )
        
    def yeni_nesne_ekle(self):
        try:
            tip = random.choice(["veri", "karanlik", "anti"])
            id_ = f"N{self.sayac:04d}"
            stabilite = random.uniform(50, 100)
            tehlike = random.randint(1, 10)
            
            if tip == "veri":
                nesne = VeriPaketi(id_, stabilite, tehlike)
       
            elif tip == "karanlik":
                nesne = KaranlikMadde(id_, stabilite, tehlike)
              
            else:
                nesne = AntiMadde(id_, stabilite, tehlike)
              
            
            self.envanter.append(nesne)
            self.sayac += 1
    
            self.guncelle_durum()
            
        except Exception as e:
            messagebox.showerror("Hata", str(e))
            
    def envanteri_listele(self):
        if not self.envanter:
            self.log_mesaj("Envanter boş.")
            return
            
        self.log_mesaj("\n" + "="*60)
        self.log_mesaj("ENVANTER RAPORU:")
        self.log_mesaj("="*60)
        
        for n in self.envanter:
            kritik_str = " [KRİTİK]" if isinstance(n, IKritik) else ""
            stabilite_bar = "" * int(n.stabilite / 10) + "" * (10 - int(n.stabilite / 10))
            self.log_mesaj(f"{n.durum_bilgisi()}{kritik_str}")
            self.log_mesaj(f"   Stabilite: [{stabilite_bar}]")
            
        self.log_mesaj("="*60 + "\n")
        
    def nesne_bul(self, id_: str) -> KuantumNesnesi | None:
        for n in self.envanter:
            if n.id == id_:
                return n
        return None
        
    def nesne_analiz(self):
        try:
            id_ = self.id_entry.get().strip()
            if not id_:
                messagebox.showwarning("Uyarı", "Lütfen bir ID girin!")
                return
                
            nesne = self.nesne_bul(id_)
            if not nesne:
                self.log_mesaj(f"Nesne bulunamadı: {id_}")
                messagebox.showerror("Hata", "Nesne bulunamadı!")
                return
                
            mesaj = nesne.analiz_et()
            self.log_mesaj(f"Analiz: {mesaj}")
            self.log_mesaj(f"   {nesne.durum_bilgisi()}")
            
            if nesne.stabilite < 30:
                self.log_mesaj(f"DİKKAT! {id_} kritik seviyede!")
                
        except KuantumCokusuException as ex:
            self.sistem_coktu(str(ex))
        except Exception as e:
            messagebox.showerror("Hata", str(e))
            
    def acil_sogutma(self):
        try:
            id_ = self.id_entry.get().strip()
            if not id_:
                messagebox.showwarning("Uyarı", "Lütfen bir ID girin!")
                return
                
            nesne = self.nesne_bul(id_)
            if not nesne:
                self.log_mesaj(f"Nesne bulunamadı: {id_}")
                messagebox.showerror("Hata", "Nesne bulunamadı!")
                return
                
            if isinstance(nesne, IKritik):
                nesne.acil_durum_sogutmasi()
                self.log_mesaj(f"Soğutma uygulandı: {id_}")
                self.log_mesaj(f"   {nesne.durum_bilgisi()}")
                messagebox.showinfo("Başarılı", "Soğutma işlemi tamamlandı!")
            else:
                self.log_mesaj(f"{id_} soğutulamaz! (IKritik değil)")
                messagebox.showwarning("Uyarı", "Bu nesne soğutulamaz!")
                
        except Exception as e:
            messagebox.showerror("Hata", str(e))
            
    def sistem_coktu(self, mesaj: str):
        self.log_mesaj("\n" + "!"*60)
        self.log_mesaj("SİSTEM ÇÖKTÜ! TAHLİYE BAŞLATILIYOR...")
        self.log_mesaj(f"{mesaj}")
        self.log_mesaj("!"*60)
        
        messagebox.showerror(
            "KUANTUM ÇÖKÜŞÜ!",
            f"SİSTEM ÇÖKTÜ!\n\n{mesaj}\n\nTAHLİYE BAŞLATILIYOR..."
        )
        
        self.root.quit()
        
    def guvenli_cikis(self):
        if messagebox.askokcancel("Çıkış", "Güvenli çıkış yapmak istiyor musunuz?"):
            self.log_mesaj("Güvenli çıkış yapılıyor...")
            self.root.quit()

def main():
    root = tk.Tk()
    app = KuantumAmbarGUI(root)
    root.mainloop()

if __name__ == "__main__":
    main()
