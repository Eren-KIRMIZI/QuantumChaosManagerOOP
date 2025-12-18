from __future__ import annotations
from abc import ABC, abstractmethod
from typing import List
import random
import sys

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
        self._stabilite = 100.0  # initialize before setter to avoid attribute errors
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
        # Stabilite her ayarlamada 0-100 arasında tutulur (clamp).
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

    # Yardımcı: stabilite düşüşleri sonrası çöküş kontrolü
    def _kuantum_cokusu_kontrol(self) -> None:
        if self.stabilite <= 0:
            raise KuantumCokusuException(self.id)

class VeriPaketi(KuantumNesnesi):
    # IKritik değildir.
    def analiz_et(self) -> None:
        # Stabilite 5 düşer, mesaj verir
        print("Veri içeriği okundu.")
        self.stabilite -= 5
        self._kuantum_cokusu_kontrol()

class KaranlikMadde(KuantumNesnesi, IKritik):
    def analiz_et(self) -> None:
        self.stabilite -= 15
        self._kuantum_cokusu_kontrol()

    def acil_durum_sogutmasi(self) -> None:
        self.stabilite = min(100.0, self.stabilite + 50)

class AntiMadde(KuantumNesnesi, IKritik):
    # Çok tehlikelidir. Her işlemde 25 düşer.
    def analiz_et(self) -> None:
        print("Evrenin dokusu titriyor...")
        self.stabilite -= 25
        self._kuantum_cokusu_kontrol()

    def acil_durum_sogutmasi(self) -> None:
        self.stabilite = min(100.0, self.stabilite + 50)

def rastgele_nesne_uret(sayac: int) -> KuantumNesnesi:
    tip = random.choice(["veri", "karanlik", "anti"])
    id_ = f"N{sayac:04d}"
    stabilite = random.uniform(50, 100)  # başlangıçta makul stabilite
    tehlike = random.randint(1, 10)
    if tip == "veri":
        return VeriPaketi(id_, stabilite, tehlike)
    elif tip == "karanlik":
        return KaranlikMadde(id_, stabilite, tehlike)
    else:
        return AntiMadde(id_, stabilite, tehlike)

def nesneyi_bul(envanter: List[KuantumNesnesi], id_: str) -> KuantumNesnesi | None:
    for n in envanter:
        if n.id == id_:
            return n
    return None

def main():
    envanter: List[KuantumNesnesi] = []
    sayac = 1
    while True:
        try:
            print("\nKUANTUM AMBARI KONTROL PANELİ")
            print("1. Yeni Nesne Ekle (Rastgele)")
            print("2. Tüm Envanteri Listele")
            print("3. Nesneyi Analiz Et (ID)")
            print("4. Acil Durum Soğutması Yap (ID, sadece IKritik)")
            print("5. Çıkış")
            secim = input("Seçiminiz: ").strip()

            if secim == "1":
                nesne = rastgele_nesne_uret(sayac)
                sayac += 1
                envanter.append(nesne)
                print(f"Eklendi -> {nesne.durum_bilgisi()}")

            elif secim == "2":
                if not envanter:
                    print("Envanter boş.")
                else:
                    for n in envanter:
                        print(n.durum_bilgisi())

            elif secim == "3":
                id_ = input("Analiz edilecek ID: ").strip()
                n = nesneyi_bul(envanter, id_)
                if not n:
                    print("Nesne bulunamadı.")
                else:
                    n.analiz_et()
                    print("Analiz tamam. " + n.durum_bilgisi())

            elif secim == "4":
                id_ = input("Soğutma uygulanacak ID: ").strip()
                n = nesneyi_bul(envanter, id_)
                if not n:
                    print("Nesne bulunamadı.")
                else:
                    if isinstance(n, IKritik):
                        n.acil_durum_sogutmasi()
                        print("Soğutma uygulandı. " + n.durum_bilgisi())
                    else:
                        print("Bu nesne soğutulamaz!")

            elif secim == "5":
                print("Güvenli çıkış yapılıyor...")
                break

            else:
                print("Geçersiz seçim.")

        except KuantumCokusuException as ex:
            print("SİSTEM ÇÖKTÜ! TAHLİYE BAŞLATILIYOR...")
            print(str(ex))
            sys.exit(1)
        except Exception as ex:
            # Beklenmeyen hatalar için nazik mesaj
            print(f"Hata: {ex}")

if __name__ == "__main__":
    main()
