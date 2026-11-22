package com.example.data.curriculum

import com.example.data.model.SubjectCategory
import com.example.data.model.SubjectDomain
import com.example.data.model.SubjectItem

object CurriculumCatalog {

    val allGrades: List<Int> = listOf(5, 6, 7, 8, 9, 10, 11, 12)

    private val middleSchoolLanguages = listOf(
        Triple("İngilizce (A1/A2)", SubjectDomain.ENGLISH, 0xFF2563EB) to listOf(
            "Greetings & Daily Routines", "Hobbies, Games & Abilities", "Health & Simple Illnesses",
            "Weather, Seasons & Emotions", "Town Places & Directions", "Past Tense & Holiday Memories"
        ),
        Triple("Almanca", SubjectDomain.GERMAN, 0xFFD97706) to listOf(
            "Begrüßung & Sich Vorstellen (Tanışma)", "Zahlen, Farben & Schulsachen", "Meine Familie & Hobbys",
            "Tagesablauf & Uhrzeiten", "Essen, Trinken & Einkaufen"
        ),
        Triple("Arapça (A1/A2)", SubjectDomain.ARABIC, 0xFF059669) to listOf(
            "Harfler, Harekeler ve Selamlaşma", "Ailem ve Okulum", "Sayılar ve Günlük İfadeler",
            "Yiyecekler ve Meslekler", "Temel Fiil Kalıpları"
        ),
        Triple("Azerbaycan Türkçesi", SubjectDomain.TURKIC_LANGUAGES, 0xFF0284C7) to listOf(
            "Salamlaşma və Tanışlıq", "Əlifba və Səs Uyumu", "Ailə, Məktəb və Gündəlik Həyat",
            "Ortak Atasözleri ve Deyimler", "Kültür ve Coğrafya Sözlüğü"
        ),
        Triple("Kazakça", SubjectDomain.TURKIC_LANGUAGES, 0xFF0EA5E9) to listOf(
            "Sälemetsiz be! (Selamlaşma)", "Otbası jäne Mektep (Aile ve Okul)", "Sandar men Kündelikti Sözder",
            "Kazak Kültürü ve Bozkır Sözlüğü", "Türkiye Türkçesi ile Ortak Kökler"
        ),
        Triple("Özbekçe", SubjectDomain.TURKIC_LANGUAGES, 0xFF0D9488) to listOf(
            "Assalomu Alaykum ve Tanışma", "Oila va Maktab (Aile ve Okul)", "Sonlar va Vaqt (Sayılar ve Zaman)",
            "Özbek Mutfağı ve Kültür Kavramları", "Ortak Kelime Hazinesi"
        ),
        Triple("Kırgızca", SubjectDomain.TURKIC_LANGUAGES, 0xFFDC2626) to listOf(
            "Salamlaşma ve Günlük Diyalog", "Üy-Bülö cana Mektep", "Manas Destanı ve Kültür Kelimeleri",
            "Sayılar, Doğa ve Mevsimler", "Ortak Türk Dili Kökleri"
        ),
        Triple("Türkmence", SubjectDomain.TURKIC_LANGUAGES, 0xFF16A34A) to listOf(
            "Salamlaşma ve Tanışma Kalıpları", "Maşgala we Mekdep (Aile ve Okul)", "Sanlar we Wagt",
            "Mahtumkulu ve Kültür Kavramları", "Oğuz Grubu Ortak Sözcükler"
        )
    )

    private val middleSchoolReligion = listOf(
        Triple("Din Kültürü ve Ahlak Bilgisi", SubjectDomain.RELIGION_ETHICS, 0xFF047857) to listOf(
            "Allah İnancı ve Esma-i Hüsna", "Ramazan, Oruç ve Namaz İbadeti", "Adap ve Nezaket Kuralları",
            "Hz. Muhammed ve Aile Hayatı", "Çevremizde Dinin İzleri ve Değerlerimiz"
        ),
        Triple("Kur'an-ı Kerim", SubjectDomain.QURAN, 0xFF0F766E) to listOf(
            "Kur'an Harfleri ve Mahreçler", "Harekeler, Cezim ve Şedde", "Temel Tecvit Kuralları (Med ve İhfa)",
            "Namaz Sureleri ve Anlamları", "Kur'an'ı Güzel Okuma ve Adabı"
        ),
        Triple("Peygamberimizin Hayatı", SubjectDomain.PROPHET_LIFE, 0xFF065F46) to listOf(
            "Peygamberimizin Çocukluğu ve Gençliği", "El-Emin Sıfatı ve Güvenilirliği", "Mekke Dönemi ve İlk Müslümanlar",
            "Hicret ve Medine Kardeşliği", "Merhamet, Hoşgörü ve İnsan Sevgisi"
        ),
        Triple("Temel Dini Bilgiler", SubjectDomain.RELIGION_ETHICS, 0xFF15803D) to listOf(
            "İmanın Şartları (Amentü)", "İslam'ın Beş Şartı", "Abdest, Teyemmüm ve Taharet",
            "Güzel Ahlak ve Kul Hakkı", "Dua ve Şükür Bilinci"
        )
    )

    private fun buildMiddleLanguages(grade: Int): List<SubjectItem> =
        middleSchoolLanguages.mapIndexed { idx, (meta, topics) ->
            SubjectItem(
                id = "g${grade}_lang_$idx",
                grade = grade,
                name = meta.first,
                category = SubjectCategory.LANGUAGES,
                domain = meta.second,
                accentHex = meta.third.toLong(),
                topics = topics
            )
        }

    private fun buildMiddleReligion(grade: Int): List<SubjectItem> =
        middleSchoolReligion.mapIndexed { idx, (meta, topics) ->
            SubjectItem(
                id = "g${grade}_rel_$idx",
                grade = grade,
                name = meta.first,
                category = SubjectCategory.RELIGION,
                domain = meta.second,
                accentHex = meta.third.toLong(),
                topics = topics
            )
        }

    private fun buildGrade5(): List<SubjectItem> = listOf(
        // Temel
        SubjectItem(
            id = "g5_turkce", grade = 5, name = "Türkçe",
            category = SubjectCategory.CORE, domain = SubjectDomain.TURKISH, accentHex = 0xFFC8102E,
            topics = listOf("Sözcükte Anlam ve Çok Anlamlılık", "Cümlede Anlam (Neden-Sonuç, Amaç)", "Paragrafta Ana Fikir ve Yardımcı Düşünce", "Ses Olayları (Ünlü Düşmesi, Benzeşme)", "Kök ve Ekler (Yapım ve Çekim Ekleri)", "Yazım Kuralları ve Noktalama İşaretleri")
        ),
        SubjectItem(
            id = "g5_mat", grade = 5, name = "Matematik",
            category = SubjectCategory.CORE, domain = SubjectDomain.MATH, accentHex = 0xFF2563EB,
            topics = listOf("Doğal Sayılar ve Bölükler", "Doğal Sayılarla Dört İşlem ve Problemler", "Kesirler ve Denk Kesirler", "Ondalık Gösterim ve Yüzdeler", "Temel Geometrik Kavramlar ve Açılar", "Çevre, Alan Ölçme ve Veri İşleme")
        ),
        SubjectItem(
            id = "g5_fen", grade = 5, name = "Fen Bilimleri",
            category = SubjectCategory.CORE, domain = SubjectDomain.SCIENCE, accentHex = 0xFF059669,
            topics = listOf("Güneş, Dünya ve Ay'ın Hareketleri", "Canlılar Dünyası ve Sınıflandırma", "Kuvvetin Ölçülmesi ve Sürtünme Kuvveti", "Maddenin Hâl Değişimi ve Isı", "Işığın Yayılması ve Gölge Oluşumu", "İnsan ve Çevre, Basit Elektrik Devreleri")
        ),
        SubjectItem(
            id = "g5_sosyal", grade = 5, name = "Sosyal Bilgiler",
            category = SubjectCategory.CORE, domain = SubjectDomain.SOCIAL_STUDIES, accentHex = 0xFFD97706,
            topics = listOf("Birey ve Toplum: Hak ve Sorumluluklar", "Anadolu ve Mezopotamya Uygarlıkları", "Yeryüzü Şekilleri ve İklim Tipleri", "Bilim, Teknoloji ve Güvenli İnternet", "Üretim, Dağıtım ve Tüketim", "Etkin Vatandaşlık ve Yönetim Birimleri")
        ),
        // Seçmeli & Alan
        SubjectItem(
            id = "g5_bilisim", grade = 5, name = "Bilişim Teknolojileri ve Yazılım",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.COMPUTER_SCIENCE, accentHex = 0xFF4F46E5,
            topics = listOf("Bilişim Etiği ve Dijital Vatandaşlık", "Donanım ve Yazılım Bileşenleri", "Algoritma ve Akış Şemaları", "Blok Tabanlı Kodlama Temelleri", "Siber Güvenlik ve Güçlü Parola")
        ),
        SubjectItem(
            id = "g5_gorsel", grade = 5, name = "Görsel Sanatlar",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.ARTS_MUSIC, accentHex = 0xFFDB2777,
            topics = listOf("Renk Bilgisi: Ana, Ara ve Sıcak-Soğuk Renkler", "Desen, Çizgi ve Doku Çalışmaları", "Perspektif ve Mekân Algısı", "Geleneksel Türk El Sanatları", "Müze Kültürü ve Sanat Eleştirisi")
        ),
        SubjectItem(
            id = "g5_muzik", grade = 5, name = "Müzik",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.ARTS_MUSIC, accentHex = 0xFF9333EA,
            topics = listOf("Nota Değerleri ve Dizek (Porte)", "Ritim, Ölçü ve Tempo Kavramları", "İstiklal Marşı ve Milli Marşlarımız", "Türk Halk Müziği Çalgıları", "Ses Eğitimi ve Doğru Nefes")
        ),
        SubjectItem(
            id = "g5_beden", grade = 5, name = "Beden Eğitimi ve Spor",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.PHYSICAL_ED, accentHex = 0xFF16A34A,
            topics = listOf("Isınma, Esneme ve Hareket Güvenliği", "Temel Jimnastik ve Denge Becerileri", "Takım Oyunları ve Fair-Play", "Atletizm: Koşu, Atlama ve Atma", "Sağlıklı Beslenme ve Aktif Yaşam")
        ),
        SubjectItem(
            id = "g5_zeka", grade = 5, name = "Zeka Oyunları",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.MIND_GAMES, accentHex = 0xFFEA580C,
            topics = listOf("Sayısal Mantık ve Örüntü Tamamlama", "Sudoku ve Kendoku Stratejileri", "Sözel Mantık ve Şifre Çözme", "Mangala ve Strateji Oyunları", "Görsel-Uzamsal Tangram ve Küp Sayma")
        ),
        SubjectItem(
            id = "g5_yazarlik", grade = 5, name = "Yazarlık ve Yazma Becerileri",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.WRITING_DRAMA, accentHex = 0xFFBE123C,
            topics = listOf("Hikâye Unsurları (Olay, Yer, Zaman, Kişi)", "Betimleyici ve Öyküleyici Anlatım", "Günlük, Anı ve Mektup Yazma", "Yaratıcı Başlık ve Giriş Cümlesi", "Metin Düzenleme ve İmla Kontrolü")
        ),
        SubjectItem(
            id = "g5_drama", grade = 5, name = "Drama",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.WRITING_DRAMA, accentHex = 0xFF7C3AED,
            topics = listOf("Beden Dili, Mimik ve Jest Kullanımı", "Doğaçlama ve Rol Oynama", "Empati ve İletişim Becerileri", "Pandomim ve Sessiz Anlatım", "Sahne Farkındalığı ve Grup Uyumu")
        ),
        SubjectItem(
            id = "g5_bilimuyg", grade = 5, name = "Bilim Uygulamaları",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.SCIENCE, accentHex = 0xFF0D9488,
            topics = listOf("Bilimsel Süreç: Gözlem ve Hipotez", "Bağımlı, Bağımsız ve Kontrollü Değişken", "Basit Laboratuvar Deney Tasarımı", "Veri Tablosu ve Grafik Okuma", "Geri Dönüşüm ve Sürdürülebilir Proje")
        )
    ) + buildMiddleLanguages(5) + buildMiddleReligion(5)

    private fun buildGrade6(): List<SubjectItem> = listOf(
        SubjectItem(
            id = "g6_turkce", grade = 6, name = "Türkçe",
            category = SubjectCategory.CORE, domain = SubjectDomain.TURKISH, accentHex = 0xFFC8102E,
            topics = listOf("Sözcükte Yapı ve Ekler", "İsimler, Sıfatlar ve Zamirler", "Edat, Bağlaç ve Ünlemler", "İsim ve Sıfat Tamlamaları", "Paragrafta Anlam ve Metin Türleri", "Söz Sanatları ve Yazım Kuralları")
        ),
        SubjectItem(
            id = "g6_mat", grade = 6, name = "Matematik",
            category = SubjectCategory.CORE, domain = SubjectDomain.MATH, accentHex = 0xFF2563EB,
            topics = listOf("Üslü İfadeler ve İşlem Önceliği", "Çarpanlar, Katlar ve Bölünebilme Kuralları", "Kümeler ve Tam Sayılar", "Kesirlerle ve Ondalık Gösterimle İşlemler", "Oran ve Cebirsel İfadeler", "Alan, Hacim Ölçme ve Çember")
        ),
        SubjectItem(
            id = "g6_fen", grade = 6, name = "Fen Bilimleri",
            category = SubjectCategory.CORE, domain = SubjectDomain.SCIENCE, accentHex = 0xFF059669,
            topics = listOf("Güneş Sistemi ve Tutulmalar", "Vücudumuzdaki Sistemler (Destek, Sindirim, Dolaşım)", "Kuvvet, Bileşke Kuvvet ve Sabit Sürat", "Maddenin Tanecikli Yapısı ve Yoğunluk", "Sesin Yayılması ve Yalıtımı", "İletken-Yalıtkan Maddeler ve Elektrik")
        ),
        SubjectItem(
            id = "g6_sosyal", grade = 6, name = "Sosyal Bilgiler",
            category = SubjectCategory.CORE, domain = SubjectDomain.SOCIAL_STUDIES, accentHex = 0xFFD97706,
            topics = listOf("Orta Asya İlk Türk Devletleri", "İslamiyet'in Doğuşu ve Türk-İslam Devletleri", "Coğrafi Konum, Paralel ve Meridyenler", "Bilim ve Teknoloji Hayatımızda", "Ülkemizin Kaynakları ve Vergi Bilinci", "Demokrasi Serüveni ve Yönetim Biçimleri")
        ),
        SubjectItem(
            id = "g6_bilisim", grade = 6, name = "Bilişim Teknolojileri ve Yazılım",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.COMPUTER_SCIENCE, accentHex = 0xFF4F46E5,
            topics = listOf("Dosya Yönetimi ve Bulut Depolama", "İnternet Ağları ve Arama Stratejileri", "Değişkenler, Koşullar ve Döngüler", "Etkileşimli Oyun ve Animasyon Kodlama", "Dijital Ayak İzi ve Telif Hakları")
        ),
        SubjectItem(
            id = "g6_gorsel", grade = 6, name = "Görsel Sanatlar",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.ARTS_MUSIC, accentHex = 0xFFDB2777,
            topics = listOf("Kompozisyon İlkeleri ve Denge", "Işık-Gölge ve Hacimlendirme", "Baskı Resim Teknikleri", "Ebru ve Minyatür Sanatı", "Üç Boyutlu Heykel ve Rölyef")
        ),
        SubjectItem(
            id = "g6_muzik", grade = 6, name = "Müzik",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.ARTS_MUSIC, accentHex = 0xFF9333EA,
            topics = listOf("Müzikte Hız ve Gürlük Terimleri", "Türk Sanat Müziği ve Makam Kavramı", "Çok Sesli Müzik ve Koro", "Bölgesel Türkülerimiz", "Ritim Kalıpları ve Senkop")
        ),
        SubjectItem(
            id = "g6_beden", grade = 6, name = "Beden Eğitimi ve Spor",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.PHYSICAL_ED, accentHex = 0xFF16A34A,
            topics = listOf("Voleybol ve Basketbol Temel Kuralları", "Dayanıklılık ve Çeviklik Egzersizleri", "Olimpiyat Ruhu ve Spor Ahlakı", "Halk Oyunları ve Ritim", "İlk Yardım ve Spor Yaralanmalarından Korunma")
        ),
        SubjectItem(
            id = "g6_zeka", grade = 6, name = "Zeka Oyunları",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.MIND_GAMES, accentHex = 0xFFEA580C,
            topics = listOf("Kriptografi ve Harf-Sayı Şifreleri", "Patika ve Çit (Slitherlink) Bulmacaları", "Mantık Tabloları ve Çıkarım", "Reversi ve Stratejik Hamleler", "Geometrik Bölme ve Simetri")
        ),
        SubjectItem(
            id = "g6_yazarlik", grade = 6, name = "Yazarlık ve Yazma Becerileri",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.WRITING_DRAMA, accentHex = 0xFFBE123C,
            topics = listOf("Bilgilendirici ve Tartışmacı Metinler", "Karakter Geliştirme ve Diyalog Yazımı", "Fabl, Masal ve Şiir Atölyesi", "Kaynakça Gösterme ve Özet Çıkarma", "Anlatım Akıcılığı ve Bağlaçlar")
        ),
        SubjectItem(
            id = "g6_drama", grade = 6, name = "Drama",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.WRITING_DRAMA, accentHex = 0xFF7C3AED,
            topics = listOf("Çatışma Çözme ve Doğaçlama", "Geleneksel Türk Tiyatrosu: Karagöz ve Ortaoyunu", "Ses Tonlama ve Vurgu Egzersizleri", "Kısa Senaryo Yazımı ve Canlandırma", "Ekip İçi Güven ve İletişim")
        ),
        SubjectItem(
            id = "g6_bilimuyg", grade = 6, name = "Bilim Uygulamaları",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.SCIENCE, accentHex = 0xFF0D9488,
            topics = listOf("Yoğunluk Kulesi ve Karışım Deneyleri", "Ses Yalıtımı Tasarım Projesi", "Yenilenebilir Enerji Modelleri", "Mikroskop Kullanımı ve Gözlem Raporu", "Bilimsel Poster Hazırlama")
        )
    ) + buildMiddleLanguages(6) + buildMiddleReligion(6)

    private fun buildGrade7(): List<SubjectItem> = listOf(
        SubjectItem(
            id = "g7_turkce", grade = 7, name = "Türkçe",
            category = SubjectCategory.CORE, domain = SubjectDomain.TURKISH, accentHex = 0xFFC8102E,
            topics = listOf("Fiiller: Anlam, Kip ve Kişi", "Ek Fiil ve Fiilde Yapı", "Zarflar (Belirteçler)", "Anlatım Bozuklukları (Anlama Dayalı)", "Paragraf Yorumlama ve Sözel Mantık", "Yazım Kuralları ve Noktalama")
        ),
        SubjectItem(
            id = "g7_mat", grade = 7, name = "Matematik",
            category = SubjectCategory.CORE, domain = SubjectDomain.MATH, accentHex = 0xFF2563EB,
            topics = listOf("Tam Sayılarla İşlemler ve Kuvvetler", "Rasyonel Sayılar ve Çok Adımlı İşlemler", "Cebirsel İfadeler ve Birinci Dereceden Denklemler", "Oran, Orantı ve Yüzde Problemleri", "Doğrular, Açılar ve Çokgenler", "Çember, Daire ve Veri Analizi")
        ),
        SubjectItem(
            id = "g7_fen", grade = 7, name = "Fen Bilimleri",
            category = SubjectCategory.CORE, domain = SubjectDomain.SCIENCE, accentHex = 0xFF059669,
            topics = listOf("Güneş Sistemi Ötesi ve Uzay Araştırmaları", "Hücre, Mitoz ve Mayoz Bölünme", "Kütle, Ağırlık, İş ve Enerji Dönüşümleri", "Saf Maddeler, Atom, Element ve Karışımlar", "Işığın Soğurulması, Aynalar ve Kırılma", "Canlılarda Üreme ve Elektrik Devreleri")
        ),
        SubjectItem(
            id = "g7_sosyal", grade = 7, name = "Sosyal Bilgiler",
            category = SubjectCategory.CORE, domain = SubjectDomain.SOCIAL_STUDIES, accentHex = 0xFFD97706,
            topics = listOf("İletişim, Empati ve Kitle İletişimi", "Osmanlı Devleti'nin Kuruluş ve Yükselme Dönemi", "Türkiye'de Nüfus, Yerleşme ve Göç", "Bilim Tarihi: İslam ve Avrupa Bilginleri", "Ekonomi, Vakıf Kültürü ve Meslekler", "Demokrasinin Serüveni ve Kurumları")
        ),
        SubjectItem(
            id = "g7_tektas", grade = 7, name = "Teknoloji ve Tasarım",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.TECH_DESIGN, accentHex = 0xFF0284C7,
            topics = listOf("Tasarım Odaklı Düşünme Süreci", "Ergonomi ve İnsan Odaklı Ürünler", "Temel Çizim, Ölçek ve Teknik Resim", "Biyomimikri (Doğadan İlham Alan Tasarım)", "İnovasyon, Patent ve Fikri Mülkiyet")
        ),
        SubjectItem(
            id = "g7_gorsel", grade = 7, name = "Görsel Sanatlar",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.ARTS_MUSIC, accentHex = 0xFFDB2777,
            topics = listOf("İki Kaçış Noktalı Perspektif", "Sanat Akımları: İzlenimcilik ve Kübizm", "Tipografi ve Afiş Tasarımı", "Türk Resim Sanatı Öncüleri", "Çağdaş Sanat ve Enstalasyon")
        ),
        SubjectItem(
            id = "g7_muzik", grade = 7, name = "Müzik",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.ARTS_MUSIC, accentHex = 0xFF9333EA,
            topics = listOf("Müzik Türleri ve Orkestra Çalgıları", "Aralık, Dizi ve Majör-Minör Tonlar", "Türk Halk Müziği Ayakları", "Dünya Müziklerinden Örnekler", "Dijital Müzik ve Ritim Yazımı")
        ),
        SubjectItem(
            id = "g7_beden", grade = 7, name = "Beden Eğitimi",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.PHYSICAL_ED, accentHex = 0xFF16A34A,
            topics = listOf("Hentbol ve Futbol Temel Stratejileri", "Fiziksel Uygunluk: Kuvvet ve Esneklik", "Atletizm ve Bayrak Yarışları", "Doğa Sporları ve Oryantiring", "Spor Kültürü ve Sağlıklı Yaşam")
        ),
        SubjectItem(
            id = "g7_medya", grade = 7, name = "Medya Okuryazarlığı",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.MEDIA_LAW_DEMOCRACY, accentHex = 0xFF4F46E5,
            topics = listOf("Medya Mesajlarını Eleştirel Okuma", "Dezenformasyon ve Doğrulama Yöntemleri", "Reklam İkna Teknikleri", "Dijital Mahremiyet ve Siber Zorbalık", "Sorumlu İçerik Üreticiliği")
        ),
        SubjectItem(
            id = "g7_hukuk", grade = 7, name = "Hukuk ve Adalet",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.MEDIA_LAW_DEMOCRACY, accentHex = 0xFF991B1B,
            topics = listOf("Adalet, Eşitlik ve Hukuk Devleti", "Hak, Özgürlük ve Sorumluluk Dengesi", "Çocuk Hakları Sözleşmesi", "Anlaşmazlıkların Barışçıl Çözümü ve Arabuluculuk", "Tüketici Hakları ve Yargı Sistemi")
        ),
        SubjectItem(
            id = "g7_halk", grade = 7, name = "Halk Kültürü",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.SOCIAL_STUDIES, accentHex = 0xFFB45309,
            topics = listOf("Sözlü Kültür: Destan, Masal, Bilmece", "Halk Müziği, Âşıklık Geleneği ve Türküler", "Geleneksel Bayramlar ve İmece Kültürü", "Anadolu Mutfağı ve Misafirperverlik", "Somut Olmayan Kültürel Mirasımız")
        ),
        SubjectItem(
            id = "g7_dusunme", grade = 7, name = "Düşünme Eğitimi",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.LOGIC, accentHex = 0xFF6D28D9,
            topics = listOf("Eleştirel ve Yaratıcı Düşünme", "Soru Sorma, Kavram ve Tanım", "Tümdengelim ve Tümevarım Akıl Yürütme", "Ön Yargı ve Safsataları Fark Etme", "Argüman Oluşturma ve Kanıtlama")
        )
    ) + buildMiddleLanguages(7) + buildMiddleReligion(7)

    private fun buildGrade8(): List<SubjectItem> = listOf(
        // Temel (LGS)
        SubjectItem(
            id = "g8_turkce", grade = 8, name = "Türkçe (LGS)",
            category = SubjectCategory.CORE, domain = SubjectDomain.TURKISH, accentHex = 0xFFC8102E, badgeTag = "LGS",
            topics = listOf("Fiilimsiler (İsim-Fiil, Sıfat-Fiil, Zarf-Fiil)", "Cümlenin Ögeleri ve Vurgu", "Fiilde Çatı (Öznesine ve Nesnesine Göre)", "Cümle Türleri ve Anlatım Bozuklukları", "Paragraf, Sözel Mantık ve Görsel Okuma", "Yazım Kuralları ve Noktalama İşaretleri")
        ),
        SubjectItem(
            id = "g8_mat", grade = 8, name = "Matematik (LGS)",
            category = SubjectCategory.CORE, domain = SubjectDomain.MATH, accentHex = 0xFF2563EB, badgeTag = "LGS",
            topics = listOf("Çarpanlar, Katlar ve EBOB-EKOK", "Üslü İfadeler ve Bilimsel Gösterim", "Kareköklü İfadeler ve Veri Analizi", "Basit Olayların Olma Olasılığı", "Cebirsel İfadeler ve Özdeşlikler", "Doğrusal Denklemler, Eşitsizlikler ve Üçgenler")
        ),
        SubjectItem(
            id = "g8_fen", grade = 8, name = "Fen Bilimleri (LGS)",
            category = SubjectCategory.CORE, domain = SubjectDomain.SCIENCE, accentHex = 0xFF059669, badgeTag = "LGS",
            topics = listOf("Mevsimler ve İklim", "DNA, Genetik Kod, Kalıtım ve Biyoteknoloji", "Katı, Sıvı ve Gaz Basıncı", "Periyodik Sistem, Kimyasal Tepkimeler, Asit-Baz", "Basit Makineler (Kaldıraç, Makara, Eğik Düzlem)", "Enerji Dönüşümleri, Fotosentez ve Elektrik Yükleri")
        ),
        SubjectItem(
            id = "g8_inkilap", grade = 8, name = "T.C. İnkılap Tarihi ve Atatürkçülük",
            category = SubjectCategory.CORE, domain = SubjectDomain.REVOLUTION_HISTORY, accentHex = 0xFFB91C1C, badgeTag = "LGS",
            topics = listOf("Bir Kahraman Doğuyor", "Milli Uyanış: Bağımsızlık Yolunda Atılan Adımlar", "Milli Bir Destan: Ya İstiklal Ya Ölüm!", "Atatürkçülük ve Çağdaşlaşan Türkiye", "Demokratikleşme Çabaları", "Atatürk Dönemi Türk Dış Politikası")
        ),
        // Seçmeli & Alan
        SubjectItem(
            id = "g8_tektas", grade = 8, name = "Teknoloji ve Tasarım",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.TECH_DESIGN, accentHex = 0xFF0284C7,
            topics = listOf("İnovatif Ürün Geliştirme ve Prototipleme", "Bilgisayar Destekli 3B Tasarım", "Enerji Verimliliği ve Akıllı Şehirler", "Ulaşım Teknolojileri ve Aerodinamik", "Tanıtım, Pazarlama ve Marka Kimliği")
        ),
        SubjectItem(
            id = "g8_gorsel", grade = 8, name = "Görsel Sanatlar",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.ARTS_MUSIC, accentHex = 0xFFDB2777,
            topics = listOf("Çağdaş Türk ve Dünya Ressamları", "Dijital Grafik ve İllüstrasyon", "Mimari Estetik ve Çevre Tasarımı", "Sanat Eleştirisi ve Estetik Yargı", "Portfolyo ve Sergi Hazırlığı")
        ),
        SubjectItem(
            id = "g8_muzik", grade = 8, name = "Müzik",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.ARTS_MUSIC, accentHex = 0xFF9333EA,
            topics = listOf("Atatürk ve Türk Müziğinin Gelişimi", "Çok Sesli Türk Müziği Bestecileri", "Akorlar, Armoni ve Eşlik", "Film ve Sahne Müzikleri", "Müzik Kültürü ve Estetik Dinleme")
        ),
        SubjectItem(
            id = "g8_beden", grade = 8, name = "Beden Eğitimi",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.PHYSICAL_ED, accentHex = 0xFF16A34A,
            topics = listOf("Sınav Döneminde Stres Yönetimi ve Egzersiz", "Takım Sporlarında Taktik ve Liderlik", "Postür (Duruş) ve Kas Sağlığı", "Atletizm ve Fiziksel Dayanıklılık", "Spor Etiği ve Fair-Play")
        ),
        SubjectItem(
            id = "g8_rehberlik", grade = 8, name = "Rehberlik ve Kariyer Planlama",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.HEALTH_GUIDANCE, accentHex = 0xFF0D9488,
            topics = listOf("Verimli Ders Çalışma ve Zaman Yönetimi", "LGS Sınav Kaygısıyla Başa Çıkma", "Kendini Tanıma: İlgi, Yetenek ve Değerler", "Lise Türleri ve Meslek Seçimi", "Hedef Belirleme ve Etkili İletişim")
        ),
        // Diller
        SubjectItem(
            id = "g8_ing", grade = 8, name = "İngilizce (LGS)",
            category = SubjectCategory.LANGUAGES, domain = SubjectDomain.ENGLISH, accentHex = 0xFF2563EB, badgeTag = "LGS",
            topics = listOf("Friendship & Accepting/Refusing Invitations", "Teen Life & In The Kitchen", "On The Phone & The Internet", "Adventures & Tourism", "Chores, Science & Natural Forces")
        ),
        SubjectItem(
            id = "g8_alm", grade = 8, name = "Almanca",
            category = SubjectCategory.LANGUAGES, domain = SubjectDomain.GERMAN, accentHex = 0xFFD97706,
            topics = listOf("Freizeit, Sport und Hobbys", "Wohnen und Mein Zimmer", "Gesundheit und Arztbesuch", "Reisen, Verkehr und Urlaub", "Perfekt (Geçmiş Zaman) Yapıları")
        ),
        SubjectItem(
            id = "g8_arapca", grade = 8, name = "Arapça",
            category = SubjectCategory.LANGUAGES, domain = SubjectDomain.ARABIC, accentHex = 0xFF059669,
            topics = listOf("Okul Kulüpleri ve Hobiler", "Şehirde Yaşam ve Yön Tarifleri", "Alışveriş ve Mevsimler", "Mazi ve Muzari Fiil Çekimleri", "Günlük Diyalog ve Okuma Parçaları")
        ),
        SubjectItem(
            id = "g8_turkdunyasi", grade = 8, name = "Türk Dünyası Dilleri",
            category = SubjectCategory.LANGUAGES, domain = SubjectDomain.TURKIC_LANGUAGES, accentHex = 0xFF0284C7,
            topics = listOf("Türk Lehçeleri Haritası (Oğuz, Kıpçak, Karluk)", "Azerbaycan ve Türkmen Türkçesi Karşılaştırması", "Kazak ve Kırgız Türkçesinde Ortak Kökler", "Özbek Türkçesi ve Kültürel Kavramlar", "Ortak Atasözleri ve Türk Dünyası Edebiyatı")
        ),
        // Din & Ahlak
        SubjectItem(
            id = "g8_dkab", grade = 8, name = "Din Kültürü ve Ahlak Bilgisi",
            category = SubjectCategory.RELIGION, domain = SubjectDomain.RELIGION_ETHICS, accentHex = 0xFF047857, badgeTag = "LGS",
            topics = listOf("Kader İnancı ve Evrensel Yasalar", "Zekât, Sadaka ve İnfak Kültürü", "Din ve Hayat (Temel Hakların Korunması)", "Hz. Muhammed'in Örnekliği (Doğruluk, İstişare)", "Kur'an-ı Kerim ve Temel Özellikleri")
        ),
        SubjectItem(
            id = "g8_kuran", grade = 8, name = "Kur'an-ı Kerim",
            category = SubjectCategory.RELIGION, domain = SubjectDomain.QURAN, accentHex = 0xFF0F766E,
            topics = listOf("Tecvit: İdgam, İklab ve Kalkale", "Vakıf ve İbtida İşaretleri", "Ayetel Kürsi ve Maun Suresi Anlamı", "Kur'an Kıssalarından Dersler", "Yüzünden Akıcı ve Kurallı Okuma")
        ),
        SubjectItem(
            id = "g8_siyer", grade = 8, name = "Peygamberimizin Hayatı",
            category = SubjectCategory.RELIGION, domain = SubjectDomain.PROPHET_LIFE, accentHex = 0xFF065F46,
            topics = listOf("Medine Sözleşmesi ve Toplumsal Barış", "Hudeybiye Antlaşması ve Stratejik Sabır", "Mekke'nin Fethi ve Genel Af", "Veda Hutbesi ve Evrensel İnsan Hakları", "Peygamberimizin Bilgi ve İstişareye Verdiği Önem")
        )
    )

    private fun buildGrade9(): List<SubjectItem> = listOf(
        // Temel
        SubjectItem(
            id = "g9_edebiyat", grade = 9, name = "Türk Dili ve Edebiyatı",
            category = SubjectCategory.CORE, domain = SubjectDomain.LITERATURE, accentHex = 0xFFC8102E,
            topics = listOf("Edebiyatın Bilim ve Güzel Sanatlarla İlişkisi", "Hikâye (Olay ve Durum Hikâyesi)", "Şiir Bilgisi: Nazım Birimi, Ölçü, Kafiye, Söz Sanatları", "Masal, Fabl ve Roman Türleri", "Tiyatro, Biyografi ve Mektup", "Dil Bilgisi: İsim, Sıfat, Zamir, Zarf ve Yazım")
        ),
        SubjectItem(
            id = "g9_mat", grade = 9, name = "Matematik",
            category = SubjectCategory.CORE, domain = SubjectDomain.MATH, accentHex = 0xFF2563EB,
            topics = listOf("Mantık (Önermeler ve Bileşik Önermeler)", "Kümeler ve Kartezyen Çarpım", "Sayı Kümeleri, Bölünebilme ve EBOB-EKOK", "Birinci Dereceden Denklem, Eşitsizlik ve Mutlak Değer", "Üslü ve Köklü İfadeler", "Oran-Orantı, Problemler ve Üçgenler")
        ),
        SubjectItem(
            id = "g9_fizik", grade = 9, name = "Fizik",
            category = SubjectCategory.CORE, domain = SubjectDomain.PHYSICS, accentHex = 0xFF0284C7,
            topics = listOf("Fizik Bilimine Giriş ve Temel-Türetilmiş Büyüklükler", "Madde ve Özellikleri (Özkütle, Adezyon, Kohezyon)", "Hareket ve Newton'un Hareket Yasaları", "İş, Güç, Enerji ve Verim", "Isı, Sıcaklık ve Genleşme", "Elektrostatik ve Yüklenme Çeşitleri")
        ),
        SubjectItem(
            id = "g9_kimya", grade = 9, name = "Kimya",
            category = SubjectCategory.CORE, domain = SubjectDomain.CHEMISTRY, accentHex = 0xFF7C3AED,
            topics = listOf("Kimya Bilimi, Simya ve Laboratuvar Güvenliği", "Atom Modelleri ve Periyodik Sistem", "Kimyasal Türler Arası Etkileşimler (İyonik, Kovalent, Metalik)", "Maddenin Hâlleri (Katı, Sıvı, Gaz, Plazma)", "Doğa ve Kimya: Su ve Çevre Kimyası")
        ),
        SubjectItem(
            id = "g9_biyoloji", grade = 9, name = "Biyoloji",
            category = SubjectCategory.CORE, domain = SubjectDomain.BIOLOGY, accentHex = 0xFF059669,
            topics = listOf("Yaşam Bilimi Biyoloji ve Canlıların Ortak Özellikleri", "İnorganik ve Organik Bileşikler (Karbonhidrat, Protein, Enzim)", "Hücre Yapısı, Organeller ve Zardan Madde Geçişi", "Canlılar Dünyası ve Sınıflandırma (Âlemler)", "Biyoçeşitlilik ve Ekosistem Koruma")
        ),
        SubjectItem(
            id = "g9_tarih", grade = 9, name = "Tarih",
            category = SubjectCategory.CORE, domain = SubjectDomain.HISTORY, accentHex = 0xFFB45309,
            topics = listOf("Tarih ve Zaman (Kaynaklar, Takvimler)", "İnsanlığın İlk Dönemleri ve İlk Çağ Uygarlıkları", "Orta Çağ'da Dünya ve Ticaret Yolları", "İlk ve Orta Çağlarda Türk Dünyası", "İslam Medeniyetinin Doğuşu", "Türklerin İslamiyet'i Kabulü ve İlk Türk-İslam Devletleri")
        ),
        SubjectItem(
            id = "g9_cografya", grade = 9, name = "Coğrafya",
            category = SubjectCategory.CORE, domain = SubjectDomain.GEOGRAPHY, accentHex = 0xFF0D9488,
            topics = listOf("Doğa, İnsan ve Coğrafyanın Bölümleri", "Dünya'nın Şekli, Hareketleri ve Coğrafi Konum", "Harita Bilgisi, Ölçek ve İzohipsler", "Atmosfer, Sıcaklık, Basınç ve İklim Tipleri", "Yerleşme Tipleri ve Bölge Kavramı")
        ),
        // Seçmeli & Alan
        SubjectItem(
            id = "g9_saglik", grade = 9, name = "Sağlık Bilgisi ve Trafik Kültürü",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.HEALTH_GUIDANCE, accentHex = 0xFF16A34A,
            topics = listOf("Bireysel ve Toplumsal Sağlık Bilinci", "Bağımlılıkla Mücadele ve Ergenlik Sağlığı", "Temel İlk Yardım Uygulamaları (ABC)", "Trafik Kuralları, İşaretler ve Yaya Güvenliği", "Sorumlu Sürücü ve Trafikte Empati")
        ),
        SubjectItem(
            id = "g9_beden", grade = 9, name = "Beden Eğitimi",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.PHYSICAL_ED, accentHex = 0xFF15803D,
            topics = listOf("Kardiyovasküler Dayanıklılık ve Antrenman", "Takım Sporlarında Savunma ve Hücum", "Bireysel Sporlar: Masa Tenisi ve Badminton", "Spor Fizyolojisi ve Nabız Kontrolü", "Olimpizm ve Fair-Play")
        ),
        SubjectItem(
            id = "g9_sanatmuzik", grade = 9, name = "Görsel Sanatlar/Müzik",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.ARTS_MUSIC, accentHex = 0xFFDB2777,
            topics = listOf("Sanat Felsefesi ve Estetik Değerlendirme", "Desen, Perspektif ve Renk Teorisi", "Türk ve Batı Müziğinde Dönemler", "Ritim, Melodi ve Armoni Analizi", "Kültürel Miras ve Sanat Eleştirisi")
        ),
        SubjectItem(
            id = "g9_proje", grade = 9, name = "Proje Tasarımı",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.TECH_DESIGN, accentHex = 0xFF4F46E5,
            topics = listOf("Problem Tanımlama ve Literatür Taraması", "Hipotez Kurma ve Araştırma Yöntemleri", "Zaman Çizelgesi ve Kaynak Planlama", "Veri Toplama, Analiz ve Raporlama", "TÜBİTAK Tarzı Proje Sunumu ve Etik")
        ),
        SubjectItem(
            id = "g9_bilgisayar", grade = 9, name = "Bilgisayar Bilimi",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.COMPUTER_SCIENCE, accentHex = 0xFF2563EB,
            topics = listOf("Bilgi İşlemsel Düşünme ve Problem Çözme", "İkili (Binary) Sayı Sistemi ve Veri Temsili", "Algoritma Tasarımı ve Karmaşıklık", "Python Programlama Temelleri (Değişkenler, Döngüler, Fonksiyonlar)", "Siber Güvenlik ve Dijital Etik")
        ),
        SubjectItem(
            id = "g9_demokrasi", grade = 9, name = "Demokrasi ve İnsan Hakları",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.MEDIA_LAW_DEMOCRACY, accentHex = 0xFF991B1B,
            topics = listOf("İnsan Haklarının Tarihsel Gelişimi", "Evrensel İnsan Hakları Beyannamesi", "Demokratik Kültür, Katılım ve Çoğulculuk", "Hukukun Üstünlüğü ve Kuvvetler Ayrılığı", "Aktif Vatandaşlık ve Sivil Toplum")
        ),
        // Diller
        SubjectItem(
            id = "g9_ing", grade = 9, name = "İngilizce (B1)",
            category = SubjectCategory.LANGUAGES, domain = SubjectDomain.ENGLISH, accentHex = 0xFF2563EB,
            topics = listOf("Studying Abroad & My Environment", "Movies, Human in Nature & Inspirational People", "Bridging Cultures & World Heritage", "Emergency and Health Problems", "Present Perfect, Conditionals & Passive Basics")
        ),
        SubjectItem(
            id = "g9_ikincidil", grade = 9, name = "İkinci Yabancı Dil (Almanca/Fransızca)",
            category = SubjectCategory.LANGUAGES, domain = SubjectDomain.GERMAN, accentHex = 0xFFD97706,
            topics = listOf("Vorstellung & Salutations (Tanışma)", "Familie, Schule und Alltag", "Essen, Trinken & La Cuisine", "Stadt, Verkehr und Wegbeschreibung", "Modalverben ve Temel Fiil Çekimleri")
        ),
        SubjectItem(
            id = "g9_arapca", grade = 9, name = "Arapça",
            category = SubjectCategory.LANGUAGES, domain = SubjectDomain.ARABIC, accentHex = 0xFF059669,
            topics = listOf("İsim Cümlesi (Mübteda ve Haber)", "Fiil Cümlesi (Fiil, Fail, Meful)", "İşaret İsimleri ve Muttasıl Zamirler", "Günlük Hayat ve Eğitim Metinleri", "İsim Tamlaması (İzafet) ve Sıfat Tamlaması")
        ),
        SubjectItem(
            id = "g9_turkdunyasi", grade = 9, name = "Türk Dünyası Dilleri",
            category = SubjectCategory.LANGUAGES, domain = SubjectDomain.TURKIC_LANGUAGES, accentHex = 0xFF0284C7,
            topics = listOf("Türk Dili Ailesi ve Ses Denklikleri", "Azerbaycan ve Türkmen Edebiyatından Metinler", "Kazak ve Kırgız Türkçesi Gramer Özellikleri", "Özbek Türkçesinde Fiil Çekimleri", "Ortak Kültür ve Köken Bilgisi")
        ),
        // Din & Ahlak
        SubjectItem(
            id = "g9_dkab", grade = 9, name = "Din Kültürü ve Ahlak Bilgisi",
            category = SubjectCategory.RELIGION, domain = SubjectDomain.RELIGION_ETHICS, accentHex = 0xFF047857,
            topics = listOf("Bilgi ve İnanç (İslam'da Bilgi Kaynakları)", "Din ve İslam (Tevhid ve Fıtrat)", "İslam ve İbadet (Mükellefiyet ve İhlas)", "Gençlik ve Değerler", "Gönül Coğrafyamız ve İslam Medeniyeti")
        ),
        SubjectItem(
            id = "g9_tdini", grade = 9, name = "Temel Dini Bilgiler",
            category = SubjectCategory.RELIGION, domain = SubjectDomain.RELIGION_ETHICS, accentHex = 0xFF15803D,
            topics = listOf("İtikat Esasları ve Tevhid", "İbadetlerde Niyet, İhlas ve Sorumluluk", "Helal, Haram ve Kul Hakkı Bilinci", "Aile, Komşuluk ve Toplumsal Ahlak", "Örnek Sahabiler ve İslam Kardeşliği")
        ),
        SubjectItem(
            id = "g9_kuran", grade = 9, name = "Kur'an-ı Kerim",
            category = SubjectCategory.RELIGION, domain = SubjectDomain.QURAN, accentHex = 0xFF0F766E,
            topics = listOf("Kur'an'ın İndiriliş ve Cem Ediliş Süreci", "Tecvit Kuralları: Med Çeşitleri ve Tenvin", "Mekki ve Medeni Surelerin Özellikleri", "Seçme Surelerin Meal ve Mesajları", "Kur'an'ı Anlama ve Hayata Yansıtma")
        ),
        SubjectItem(
            id = "g9_siyer", grade = 9, name = "Peygamberimizin Hayatı",
            category = SubjectCategory.RELIGION, domain = SubjectDomain.PROPHET_LIFE, accentHex = 0xFF065F46,
            topics = listOf("Cahiliye Dönemi ve İlk Vahiy", "Hılfu'l-Fudul ve Adalet Anlayışı", "Hicret, Mescid-i Nebevi ve Suffe Ashabı", "Peygamberimizin Gençlerle İletişimi", "Üsve-i Hasene: Örnek Ahlak ve Liderlik")
        )
    )

    private fun buildGrade10(): List<SubjectItem> = listOf(
        // Temel
        SubjectItem(
            id = "g10_edebiyat", grade = 10, name = "Türk Dili ve Edebiyatı",
            category = SubjectCategory.CORE, domain = SubjectDomain.LITERATURE, accentHex = 0xFFC8102E,
            topics = listOf("Edebiyatın Tarih ve Dinle İlişkisi, Dönemler", "Dede Korkut, Halk Hikâyesi ve Mesnevi", "İslamiyet Öncesi, Halk ve Divan Şiiri", "Destan, Efsane ve Tanzimat-Servetifünun Romanı", "Geleneksel ve Modern Türk Tiyatrosu", "Fiilimsiler, İsim Tamlamaları ve Cümle Türleri")
        ),
        SubjectItem(
            id = "g10_mat", grade = 10, name = "Matematik",
            category = SubjectCategory.CORE, domain = SubjectDomain.MATH, accentHex = 0xFF2563EB,
            topics = listOf("Sayma, Permütasyon, Kombinasyon, Binom ve Olasılık", "Fonksiyon Kavramı, Bileşke ve Ters Fonksiyon", "Polinomlar ve Çarpanlara Ayırma", "İkinci Dereceden Denklemler ve Karmaşık Sayılar", "Dörtgenler ve Çokgenler", "Uzay Geometri: Prizma ve Piramit")
        ),
        SubjectItem(
            id = "g10_fizik", grade = 10, name = "Fizik",
            category = SubjectCategory.CORE, domain = SubjectDomain.PHYSICS, accentHex = 0xFF0284C7,
            topics = listOf("Elektrik Akımı, Ohm Yasası ve Devreler", "Mıknatıslar ve Manyetik Alan", "Basınç ve Kaldırma Kuvveti (Arşimet)", "Dalgalar: Yay, Su, Ses ve Deprem Dalgaları", "Optik: Aydınlanma, Gölge, Aynalar, Kırılma ve Mercekler")
        ),
        SubjectItem(
            id = "g10_kimya", grade = 10, name = "Kimya",
            category = SubjectCategory.CORE, domain = SubjectDomain.CHEMISTRY, accentHex = 0xFF7C3AED,
            topics = listOf("Kimyanın Temel Kanunları (Kütlenin Korunumu, Sabit Oranlar)", "Mol Kavramı ve Kimyasal Hesaplamalar", "Karışımlar ve Ayırma Yöntemleri", "Asitler, Bazlar ve Tuzlar (pH)", "Kimya Her Yerde: Polimerler, Sabun, Deterjan")
        ),
        SubjectItem(
            id = "g10_biyoloji", grade = 10, name = "Biyoloji",
            category = SubjectCategory.CORE, domain = SubjectDomain.BIOLOGY, accentHex = 0xFF059669,
            topics = listOf("Mitoz ve Eşeysiz Üreme", "Mayoz ve Eşeyli Üreme", "Kalıtımın Genel İlkeleri ve Mendel Genetiği", "Ekosistem Ekolojisi ve Besin Piramidi", "Güncel Çevre Sorunları ve Madde Döngüleri")
        ),
        SubjectItem(
            id = "g10_tarih", grade = 10, name = "Tarih",
            category = SubjectCategory.CORE, domain = SubjectDomain.HISTORY, accentHex = 0xFFB45309,
            topics = listOf("Yerleşme ve Devletleşme Sürecinde Selçuklu Türkiyesi", "Beylikten Devlete Osmanlı Siyaseti", "Devletleşme Sürecinde Savaşçılar ve Askerler (Tımar, Yeniçeri)", "Beylikten Devlete Osmanlı Medeniyeti", "Dünya Gücü Osmanlı (1453-1595)", "Sultan ve Osmanlı Merkez Teşkilatı")
        ),
        SubjectItem(
            id = "g10_cografya", grade = 10, name = "Coğrafya",
            category = SubjectCategory.CORE, domain = SubjectDomain.GEOGRAPHY, accentHex = 0xFF0D9488,
            topics = listOf("İç Kuvvetler, Levha Tektoniği ve Kayaçlar", "Dış Kuvvetler ve Türkiye'nin Yer Şekilleri", "Su, Toprak ve Bitki Varlığı", "Nüfus Piramitleri ve Göç Hareketleri", "Ekonomik Faaliyet Türleri ve Doğal Afetler")
        ),
        SubjectItem(
            id = "g10_felsefe", grade = 10, name = "Felsefe",
            category = SubjectCategory.CORE, domain = SubjectDomain.PHILOSOPHY, accentHex = 0xFF6D28D9,
            topics = listOf("Felsefeyi Tanıma ve Felsefi Düşüncenin Özellikleri", "Felsefe ile Düşünme: Akıl Yürütme ve Argüman", "Varlık Felsefesi (Ontoloji) ve Bilgi Felsefesi (Epistemoloji)", "Bilim, Ahlak, Din, Siyaset ve Sanat Felsefesi", "Felsefi Okuma ve Yazma")
        ),
        // Seçmeli & Alan
        SubjectItem(
            id = "g10_astronomi", grade = 10, name = "Astronomi ve Uzay Bilimleri",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.ASTRONOMY, accentHex = 0xFF1E3A8A,
            topics = listOf("Astronominin Tarihçesi ve Gözlem Araçları", "Gök Küresi, Koordinatlar ve Takımyıldızlar", "Güneş Sistemi, Ötegezegenler ve Kepler Yasaları", "Yıldızların Evrimi, Süpernova ve Kara Delikler", "Galaksiler, Kozmoloji ve Uzay Teknolojileri")
        ),
        SubjectItem(
            id = "g10_ekonomi", grade = 10, name = "Ekonomi",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.ECONOMY_ENTREPRENEURSHIP, accentHex = 0xFF047857,
            topics = listOf("Kıtlık, Seçim ve Fırsat Maliyeti", "Arz, Talep ve Piyasa Dengesi", "Para, Bankacılık ve Enflasyon", "Milli Gelir, Büyüme ve Kalkınma", "Finansal Okuryazarlık ve Bütçe Yönetimi")
        ),
        SubjectItem(
            id = "g10_girisim", grade = 10, name = "Girişimcilik",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.ECONOMY_ENTREPRENEURSHIP, accentHex = 0xFFEA580C,
            topics = listOf("Girişimcilik Kültürü ve Yenilikçi Fikirler", "İş Modeli Kanvası ve Değer Önerisi", "Pazar Araştırması ve Hedef Kitle", "Finansman Kaynakları ve Maliyet Analizi", "Sosyal Girişimcilik ve İş Etiği")
        ),
        SubjectItem(
            id = "g10_diksiyon", grade = 10, name = "Diksiyon ve Hitabet",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.COMMUNICATION_DICTION, accentHex = 0xFFBE123C,
            topics = listOf("Diyafram Nefesi ve Sesin Doğru Kullanımı", "Boğumlama (Artikülasyon), Vurgu ve Tonlama", "Türkçenin Doğru Telaffuzu ve Söyleyiş Hataları", "Beden Dili, Göz Teması ve Sahne Hâkimiyeti", "İkna Edici Hitabet ve Hazırlıklı Konuşma")
        ),
        SubjectItem(
            id = "g10_osmanlica", grade = 10, name = "Osmanlı Türkçesi",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.OTTOMAN_TURKISH, accentHex = 0xFF92400E,
            topics = listOf("Osmanlı Türkçesi Alfabesi ve Harf Birleşimleri", "Türkçe Kelimelerde Okutucu Harfler (Elif, Vav, Ye, He)", "Eklerin Yazılışı ve Kalıplaşmış İmla", "Arapça ve Farsça Tamlamalar", "Matbu Kitabe ve Arşiv Metinleri Okuma")
        ),
        SubjectItem(
            id = "g10_iletisim", grade = 10, name = "İletişim ve Sunum Becerileri",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.COMMUNICATION_DICTION, accentHex = 0xFF0284C7,
            topics = listOf("İletişim Süreci ve Etkin Dinleme", "İletişim Engelleri ve Çatışma Yönetimi", "Etkili Sunum Tasarımı ve Görsel Hiyerarşi", "Topluluk Önünde Konuşma Heyecanını Yönetme", "Dijital İletişim ve Profesyonel E-posta Adabı")
        ),
        // Diller (9. sınıf dilleri)
        SubjectItem(
            id = "g10_ing", grade = 10, name = "İngilizce (B1)",
            category = SubjectCategory.LANGUAGES, domain = SubjectDomain.ENGLISH, accentHex = 0xFF2563EB,
            topics = listOf("School Life & Future Plans (Be Going To / Will)", "Legendary Figures & Past Continuous", "Traditions (Used to) & Travel", "Helpful Tips & Conditionals (Type 1-2)", "Modern Heroes, Shopping & Relative Clauses")
        ),
        SubjectItem(
            id = "g10_ikincidil", grade = 10, name = "İkinci Yabancı Dil (Almanca/Fransızca)",
            category = SubjectCategory.LANGUAGES, domain = SubjectDomain.GERMAN, accentHex = 0xFFD97706,
            topics = listOf("Tagesablauf ve Trennbare Verben", "Kleidung, Einkaufen ve Preise", "Gesundheit, Sport ve Fitness", "Urlaub, Reisen ve Perfekt", "Kültürel Diyaloglar ve Okuma Anlama")
        ),
        SubjectItem(
            id = "g10_arapca", grade = 10, name = "Arapça",
            category = SubjectCategory.LANGUAGES, domain = SubjectDomain.ARABIC, accentHex = 0xFF059669,
            topics = listOf("Emir Fiili ve Olumsuz Emir (Nehiy)", "İnne ve Kâne Kardeşleri", "Sayıların Temyizi ve Saatler", "Kültür, Seyahat ve İletişim Metinleri", "Çeviri ve Kompozisyon Alıştırmaları")
        ),
        SubjectItem(
            id = "g10_turkdunyasi", grade = 10, name = "Türk Dünyası Dilleri",
            category = SubjectCategory.LANGUAGES, domain = SubjectDomain.TURKIC_LANGUAGES, accentHex = 0xFF0284C7,
            topics = listOf("Türk Dünyası Ortak Kültür ve Destanları", "Oğuz ve Kıpçak Grubu Metin Okumaları", "Çağdaş Türk Şairleri ve Ortak Kavramlar", "Türk Lehçelerinde Atasözleri", "Karşılaştırmalı Söz Varlığı")
        ),
        // Din & Ahlak (9. sınıf + İslam Kültür ve Medeniyeti)
        SubjectItem(
            id = "g10_dkab", grade = 10, name = "Din Kültürü ve Ahlak Bilgisi",
            category = SubjectCategory.RELIGION, domain = SubjectDomain.RELIGION_ETHICS, accentHex = 0xFF047857,
            topics = listOf("Allah-İnsan İlişkisi ve İbadet", "Hz. Muhammed ve Gençlik", "Din ve Sosyal Değişim", "Ahlaki Tutum ve Davranışlar", "İslam Düşüncesinde İtikadi ve Fıkhi Yorumlar")
        ),
        SubjectItem(
            id = "g10_tdini", grade = 10, name = "Temel Dini Bilgiler",
            category = SubjectCategory.RELIGION, domain = SubjectDomain.RELIGION_ETHICS, accentHex = 0xFF15803D,
            topics = listOf("Kur'an ve Sünnet Bütünlüğü", "İslam'da İbadetlerin Hikmetleri", "İslam Ahlakında Sabır, Şükür ve İffet", "Ticaret Ahlakı ve Helal Kazanç", "Sosyal Dayanışma ve Vakıf Ruhu")
        ),
        SubjectItem(
            id = "g10_kuran", grade = 10, name = "Kur'an-ı Kerim",
            category = SubjectCategory.RELIGION, domain = SubjectDomain.QURAN, accentHex = 0xFF0F766E,
            topics = listOf("Tecvit: Ra Hükmü, Zamir ve Sekte", "Yasin ve Mülk Surelerinden Seçme Bölümler", "Kur'an'da Aklı Kullanma ve Tefekkür", "Kıssaların Evrensel Mesajları", "Doğru Telaffuz ve Kıraat Uygulamaları")
        ),
        SubjectItem(
            id = "g10_siyer", grade = 10, name = "Peygamberimizin Hayatı",
            category = SubjectCategory.RELIGION, domain = SubjectDomain.PROPHET_LIFE, accentHex = 0xFF065F46,
            topics = listOf("Peygamberimizin Eğitim ve Öğretim Metodları", "Aile İçi İletişim ve Şefkat Örnekleri", "İstişare ve Karar Alma Kültürü", "Toplumsal Adalet ve Zayıfları Koruma", "Medine'de Bilim ve Sanatın Temelleri")
        ),
        SubjectItem(
            id = "g10_islamkultur", grade = 10, name = "İslam Kültür ve Medeniyeti",
            category = SubjectCategory.RELIGION, domain = SubjectDomain.ISLAMIC_CULTURE, accentHex = 0xFF065F46,
            topics = listOf("İslam Medeniyetinin Doğuşu ve Temel Kaynakları", "Beytülhikme ve İslam Dünyasında Bilim (İbn Sina, Harezmi)", "Eğitim Kurumları: Medrese, Kütüphane ve Rasathane", "İslam Sanatı: Hat, Tezhip, Mimari ve Musiki", "Vakıf Medeniyeti ve Şehir Kültürü")
        )
    )

    private fun buildGrade11(): List<SubjectItem> = listOf(
        // Temel
        SubjectItem(
            id = "g11_edebiyat", grade = 11, name = "Türk Dili ve Edebiyatı",
            category = SubjectCategory.CORE, domain = SubjectDomain.LITERATURE, accentHex = 0xFFC8102E,
            topics = listOf("Edebiyat ve Toplum, Edebi Akımlar (Klasisizm-Realizm)", "Cumhuriyet Dönemi Hikâye ve Roman", "Tanzimat, Servetifünun, Fecriati ve Milli Edebiyat Şiiri", "Makale, Sohbet, Fıkra, Eleştiri ve Mülakat", "Cümlenin Ögeleri ve Anlatım Bozuklukları")
        ),
        SubjectItem(
            id = "g11_tarih", grade = 11, name = "Tarih",
            category = SubjectCategory.CORE, domain = SubjectDomain.HISTORY, accentHex = 0xFFB45309,
            topics = listOf("Değişen Dünya Dengeleri Karşısında Osmanlı Siyaseti (1595-1774)", "Değişim Çağında Avrupa ve Osmanlı (Lale Devri, Islahatlar)", "Uluslararası İlişkilerde Denge Stratejisi (1774-1914)", "Devrimler Çağında Değişen Devlet-Toplum İlişkileri", "Sermaye ve Emek: Sanayi İnkılabı ve Osmanlı Ekonomisi")
        ),
        SubjectItem(
            id = "g11_felsefe", grade = 11, name = "Felsefe",
            category = SubjectCategory.CORE, domain = SubjectDomain.PHILOSOPHY, accentHex = 0xFF6D28D9,
            topics = listOf("MÖ 6. Yüzyıl - MS 2. Yüzyıl İlk Çağ Felsefesi (Sokrates, Platon, Aristoteles)", "MS 2. Yüzyıl - MS 15. Yüzyıl Hristiyan ve İslam Felsefesi (Farabi, İbn Rüşd)", "15-17. Yüzyıl Rönesans ve Akıl Çağı (Descartes, Spinoza)", "18-19. Yüzyıl Aydınlanma Felsefesi (Kant, Hegel)", "20. Yüzyıl Çağdaş Felsefe Akımları (Varoluşçuluk, Fenomenoloji)")
        ),
        // Seçmeli & Alan
        SubjectItem(
            id = "g11_mat_ayt", grade = 11, name = "İleri Matematik (AYT)",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.MATH, accentHex = 0xFF2563EB, badgeTag = "AYT",
            topics = listOf("Trigonometri (Birim Çember, Özdeşlikler, Teoremler)", "Analitik Geometri (Doğrunun Analitiği ve Eğim)", "Fonksiyonlarda Uygulamalar ve Parabol", "İkinci Dereceden Denklem ve Eşitsizlik Sistemleri", "Çember, Daire ve Uzay Geometri (Silindir, Koni, Küre)", "Koşullu Olasılık ve Bağımsız Olaylar")
        ),
        SubjectItem(
            id = "g11_fizik_ayt", grade = 11, name = "Fizik (AYT)",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.PHYSICS, accentHex = 0xFF0284C7, badgeTag = "AYT",
            topics = listOf("Vektörler ve Bağıl Hareket", "Newton'un Hareket Yasaları ve Bir Boyutta İvmeli Hareket", "İki Boyutta Hareket (Atışlar), İş-Enerji ve İtme-Momentum", "Tork, Denge, Ağırlık Merkezi ve Basit Makineler", "Elektriksel Kuvvet, Elektrik Alan, Potansiyel ve Sığa", "Manyetizma ve Elektromanyetik İndüklenme")
        ),
        SubjectItem(
            id = "g11_kimya_ayt", grade = 11, name = "Kimya (AYT)",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.CHEMISTRY, accentHex = 0xFF7C3AED, badgeTag = "AYT",
            topics = listOf("Modern Atom Teorisi, Kuantum Sayıları ve Periyodik Özellikler", "Gazlar ve İdeal Gaz Yasası (PV=nRT)", "Sıvı Çözeltiler ve Koligatif Özellikler", "Kimyasal Tepkimelerde Enerji (Entalpi, Hess Yasası)", "Kimyasal Tepkimelerde Hız ve Denge (Kc, Kp)", "Asit-Baz Dengesi ve Çözünürlük Dengesi (Kçç)")
        ),
        SubjectItem(
            id = "g11_biyoloji_ayt", grade = 11, name = "Biyoloji (AYT)",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.BIOLOGY, accentHex = 0xFF059669, badgeTag = "AYT",
            topics = listOf("Sinir Sistemi, Endokrin Sistem ve Duyu Organları", "Destek ve Hareket, Sindirim Sistemi", "Dolaşım, Bağışıklık ve Solunum Sistemi", "Üriner Sistem ve Üreme-Embriyonik Gelişim", "Komünite ve Popülasyon Ekolojisi")
        ),
        SubjectItem(
            id = "g11_turkkultur", grade = 11, name = "Türk Kültür ve Medeniyet Tarihi",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.HISTORY, accentHex = 0xFF9A3412,
            topics = listOf("Türklerde Devlet Teşkilatı (Kut, Kurultay, Divan)", "Türklerde Toplum Yapısı ve Sosyal Hayat", "Türklerde Hukuk (Töre, Şer'i ve Örfi Hukuk)", "Türklerde Ekonomi, Ahilik ve Lonca Teşkilatı", "Türklerde Eğitim, Bilim ve Sanat Tarihi")
        ),
        SubjectItem(
            id = "g11_cografya", grade = 11, name = "Coğrafya",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.GEOGRAPHY, accentHex = 0xFF0D9488, badgeTag = "AYT",
            topics = listOf("Biyoçeşitlilik, Biyomlar ve Madde Döngüleri", "Nüfus Politikaları ve Şehirlerin Fonksiyonları", "Türkiye'de Ekonomi: Tarım, Hayvancılık, Madenler ve Sanayi", "Kültür Bölgeleri ve Küresel Ticaret", "Uluslararası Örgütler ve Çevre Sorunları")
        ),
        SubjectItem(
            id = "g11_psikoloji", grade = 11, name = "Psikoloji",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.PSYCHOLOGY, accentHex = 0xFFEC4899,
            topics = listOf("Psikoloji Bilimini Tanıma ve Yaklaşımlar", "Psikolojinin Temel Süreçleri: Duyum, Algı ve Güdülenme", "Öğrenme, Bellek ve Düşünme (Koşullanma Türleri)", "Ruh Sağlığının Temelleri ve Savunma Mekanizmaları", "Gelişim Psikolojisi ve Kişilik Kuramları")
        ),
        SubjectItem(
            id = "g11_sosyoloji", grade = 11, name = "Sosyoloji",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.SOCIOLOGY, accentHex = 0xFF8B5CF6,
            topics = listOf("Sosyolojiye Giriş ve Araştırma Yöntemleri", "Birey, Toplum, Toplumsallaşma ve Statü-Rol", "Toplumsal Yapı, Tabakalaşma ve Hareketlilik", "Toplumsal Değişme ve Gelişme", "Toplum, Kültür ve Toplumsal Kurumlar (Aile, Eğitim, Ekonomi)")
        ),
        SubjectItem(
            id = "g11_mantik", grade = 11, name = "Mantık",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.LOGIC, accentHex = 0xFF4F46E5,
            topics = listOf("Mantığa Giriş ve Akıl İlkeleri (Özdeşlik, Çelişmezlik)", "Klasik Mantık: Kavram, Terim, İçlem ve Kaplam", "Önermeler, Karşı Olum Karesi ve Kıyas", "Mantık ve Dil: Anlam ve Safsatalar", "Sembolik Mantık ve Doğruluk Tabloları")
        ),
        SubjectItem(
            id = "g11_diksiyon", grade = 11, name = "Diksiyon ve Hitabet",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.COMMUNICATION_DICTION, accentHex = 0xFFBE123C,
            topics = listOf("Retorik ve İkna Sanatının İlkeleri (Ethos, Pathos, Logos)", "Sesin Tınısı, Ritim ve Duraklama Teknikleri", "Münazara ve Akademik Konuşma Becerileri", "Kitle Psikolojisi ve Etkili Hitap", "Doğaçlama Konuşma ve Kriz Yönetimi")
        ),
        SubjectItem(
            id = "g11_osmanlica", grade = 11, name = "Osmanlı Türkçesi",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.OTTOMAN_TURKISH, accentHex = 0xFF92400E,
            topics = listOf("Arapça Kökenli Kelime Kalıpları (Vezinler)", "Farsça Ekler ve Birleşik Kelimeler", "Rika Hattı Okuma Becerileri", "Divan Edebiyatı ve Tarih Metinleri Okuma", "Osmanlı Arşiv Belgeleri ve Ferman Örnekleri")
        ),
        SubjectItem(
            id = "g11_iletisim", grade = 11, name = "İletişim ve Sunum Becerileri",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.COMMUNICATION_DICTION, accentHex = 0xFF0284C7,
            topics = listOf("Kurumsal ve Akademik İletişim", "Hikâyeleştirme (Storytelling) ile Sunum", "Veri Görselleştirme ve İnfografik Kullanımı", "Mülakat ve Profesyonel İfade Teknikleri", "Soru-Cevap Oturumu Yönetimi")
        ),
        // Diller
        SubjectItem(
            id = "g11_ing", grade = 11, name = "İngilizce (B2)",
            category = SubjectCategory.LANGUAGES, domain = SubjectDomain.ENGLISH, accentHex = 0xFF2563EB,
            topics = listOf("Future Jobs & Hobbies/Skills", "Hard Times, What a Life & Back to the Past", "Open Your Heart (Wish Clauses & Type 3 Conditionals)", "Facts About Turkey (Passive Voice) & Sports", "Reported Speech, Modals of Deduction & Reading Comprehension")
        ),
        SubjectItem(
            id = "g11_ydedebiyat", grade = 11, name = "Yabancı Dil Edebiyatı",
            category = SubjectCategory.LANGUAGES, domain = SubjectDomain.FOREIGN_LIT, accentHex = 0xFF7C3AED,
            topics = listOf("Elements of Short Story & Literary Devices", "Shakespearean Drama & Sonnets", "19th Century British & American Novels", "Modern Poetry Analysis & Imagery", "Critical Reading and Comparative Literature")
        ),
        SubjectItem(
            id = "g11_alm", grade = 11, name = "Almanca",
            category = SubjectCategory.LANGUAGES, domain = SubjectDomain.GERMAN, accentHex = 0xFFD97706,
            topics = listOf("Berufe, Zukunftspläne und Ausbildung", "Umwelt, Natur und Nachhaltigkeit", "Medien, Technologie ve Kommunikation", "Nebensätze (weil, dass, obwohl, wenn)", "Präteritum ve Konjunktiv II Temelleri")
        ),
        SubjectItem(
            id = "g11_arapca", grade = 11, name = "Arapça",
            category = SubjectCategory.LANGUAGES, domain = SubjectDomain.ARABIC, accentHex = 0xFF059669,
            topics = listOf("Meçhul (Edilgen) Fiil ve Naib-i Fail", "İsmi Fail ve İsmi Meful Kalıpları", "Bağlaçlar ve Şart Cümleleri", "Edebi ve Kültürel Arapça Metinler", "Tercüme ve İleri Okuma Becerileri")
        ),
        SubjectItem(
            id = "g11_turkdunyasi", grade = 11, name = "Türk Dünyası Dilleri",
            category = SubjectCategory.LANGUAGES, domain = SubjectDomain.TURKIC_LANGUAGES, accentHex = 0xFF0284C7,
            topics = listOf("Köktürk ve Uygur Metinlerinden Günümüze Dil Köprüsü", "Çağdaş Azerbaycan ve Türkmen Edebi Metinleri", "Kazak ve Kırgız Edebiyatında Cengiz Aytmatov & Abay", "Özbek Edebiyatı ve Ali Şir Nevai", "Türk Dünyasında Ortak Terminoloji")
        ),
        // Din & Ahlak
        SubjectItem(
            id = "g11_dkab", grade = 11, name = "Din Kültürü ve Ahlak Bilgisi",
            category = SubjectCategory.RELIGION, domain = SubjectDomain.RELIGION_ETHICS, accentHex = 0xFF047857,
            topics = listOf("Dünya ve Ahiret Hayatı", "Kur'an'a Göre Hz. Muhammed", "Kur'an'da Bazı Kavramlar (Hidayet, İhsan, Takva, Sırat-ı Müstakim)", "İnançla İlgili Meseleler (Deizm, Materyalizm, Sekülerizm)", "Yahudilik ve Hristiyanlık (Temel Özellikler)")
        ),
        SubjectItem(
            id = "g11_kuran", grade = 11, name = "Kur'an-ı Kerim",
            category = SubjectCategory.RELIGION, domain = SubjectDomain.QURAN, accentHex = 0xFF0F766E,
            topics = listOf("Kur'an'ın Ana Konuları (İnanç, İbadet, Ahlak, Kıssa)", "Hucurat ve Lokman Sureleri Mesajları", "Tefsir, Meal ve Çeviri İlkeleri", "İleri Tecvit ve Kıraat Bilgisi", "Kur'an'da Sosyal Adalet ve Kardeşlik")
        ),
        SubjectItem(
            id = "g11_siyer", grade = 11, name = "Peygamberimizin Hayatı",
            category = SubjectCategory.RELIGION, domain = SubjectDomain.PROPHET_LIFE, accentHex = 0xFF065F46,
            topics = listOf("Peygamberimizin Tebliğ ve Davet Stratejisi", "Sünnetin Dindeki Yeri ve Hadis Usulüne Giriş", "Peygamberimizin Bilim, Sanat ve Çevreye Bakışı", "Farklı Kültür ve İnançlarla Birlikte Yaşama", "Veda Hutbesi'nin Güncel Evrensel İlkeleri")
        )
    )

    private fun buildGrade12(): List<SubjectItem> = listOf(
        // Temel (YKS)
        SubjectItem(
            id = "g12_edebiyat", grade = 12, name = "Türk Dili ve Edebiyatı (AYT)",
            category = SubjectCategory.CORE, domain = SubjectDomain.LITERATURE, accentHex = 0xFFC8102E, badgeTag = "YKS / AYT",
            topics = listOf("Cumhuriyet Dönemi Şiiri (Saf Şiir, Garip, İkinci Yeni, Toplumcu)", "Cumhuriyet Dönemi Romanı (1950-1980 ve Postmodernizm)", "İslamiyet Öncesi, Halk ve Divan Edebiyatı AYT Tekrar", "Tanzimat, Servetifünun ve Milli Edebiyat Sanatçıları", "Edebi Akımlar, Söz Sanatları ve Şiir İnceleme", "Deneme, Söylev ve Modern Tiyatro")
        ),
        SubjectItem(
            id = "g12_inkilap", grade = 12, name = "T.C. İnkılap Tarihi ve Atatürkçülük",
            category = SubjectCategory.CORE, domain = SubjectDomain.REVOLUTION_HISTORY, accentHex = 0xFFB91C1C, badgeTag = "YKS",
            topics = listOf("20. Yüzyıl Başlarında Osmanlı Devleti ve I. Dünya Savaşı", "Milli Mücadele Hazırlık Dönemi (Kongreler ve TBMM)", "Kurtuluş Savaşı Cepheleri ve Lozan Barış Antlaşması", "Atatürk İlkeleri ve İnkılapları", "İki Savaş Arası Türkiye ve Atatürk Dönemi Dış Politikası", "II. Dünya Savaşı ve Sonrasında Türkiye")
        ),
        // Seçmeli & Alan
        SubjectItem(
            id = "g12_mat_ayt", grade = 12, name = "Matematik (AYT)",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.MATH, accentHex = 0xFF2563EB, badgeTag = "YKS / AYT",
            topics = listOf("Üstel ve Logaritmik Fonksiyonlar", "Diziler (Aritmetik ve Geometrik Diziler)", "Trigonometri (Toplam-Fark, Yarım Açı, Denklemler)", "Limit ve Süreklilik", "Türev ve Maksimum-Minimum Uygulamaları", "İntegral, Alan Hesabı ve Çemberin Analitiği")
        ),
        SubjectItem(
            id = "g12_fizik_ayt", grade = 12, name = "Fizik (AYT)",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.PHYSICS, accentHex = 0xFF0284C7, badgeTag = "YKS / AYT",
            topics = listOf("Düzgün Çembersel Hareket ve Açısal Momentum", "Kütle Çekim Kuvveti ve Kepler Yasaları", "Basit Harmonik Hareket", "Dalga Mekaniği (Kırınım, Girişim, Doppler)", "Atom Fiziğine Giriş ve Radyoaktivite", "Modern Fizik: Fotoelektrik Olay, Compton ve Özel Görelilik")
        ),
        SubjectItem(
            id = "g12_kimya_ayt", grade = 12, name = "Kimya (AYT)",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.CHEMISTRY, accentHex = 0xFF7C3AED, badgeTag = "YKS / AYT",
            topics = listOf("Kimya ve Elektrik: Redoks, Elektrokimyasal Piller ve Elektroliz", "Karbon Kimyasına Giriş, Hibritleşme ve VSEPR", "Hidrokarbonlar (Alkan, Alken, Alkin, Aromatikler)", "Fonksiyonel Gruplar: Alkol, Eter, Aldehit, Keton, Karboksilik Asit", "Enerji Kaynakları ve Nanoteknoloji")
        ),
        SubjectItem(
            id = "g12_biyoloji_ayt", grade = 12, name = "Biyoloji (AYT)",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.BIOLOGY, accentHex = 0xFF059669, badgeTag = "YKS / AYT",
            topics = listOf("Genden Proteine: DNA Replikasyonu ve Protein Sentezi", "Genetik Mühendisliği, CRISPR ve Biyoteknoloji", "Canlılarda Enerji Dönüşümleri: Fotosentez ve Kemosentez", "Hücresel Solunum (Oksijenli, Oksijensiz, Fermantasyon)", "Bitki Biyolojisi: Dokular, Hormonlar ve Taşıma")
        ),
        SubjectItem(
            id = "g12_cagdas", grade = 12, name = "Çağdaş Türk ve Dünya Tarihi",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.HISTORY, accentHex = 0xFF9A3412, badgeTag = "AYT",
            topics = listOf("20. Yüzyıl Başlarında Dünya ve 1929 Ekonomik Buhranı", "II. Dünya Savaşı ve Sonuçları", "Soğuk Savaş Dönemi (NATO, Varşova Paktı, Kore)", "Yumuşama (Detant) Dönemi ve Kıbrıs Barış Harekâtı", "Küreselleşen Dünya, SSCB'nin Dağılması ve Türk Cumhuriyetleri")
        ),
        SubjectItem(
            id = "g12_cografya", grade = 12, name = "Coğrafya",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.GEOGRAPHY, accentHex = 0xFF0D9488, badgeTag = "AYT",
            topics = listOf("Ekstrem Doğa Olayları ve Küresel İklim Değişikliği", "Türkiye'nin Bölgesel Kalkınma Projeleri (GAP, DAP, KOP)", "Türkiye'de Ulaşım, Ticaret ve Turizm", "Ülkelerin Gelişmişlik Düzeyleri ve Küresel Etkileşim", "Jeopolitik Konum ve Enerji Koridorları")
        ),
        SubjectItem(
            id = "g12_mantik", grade = 12, name = "Mantık",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.LOGIC, accentHex = 0xFF4F46E5, badgeTag = "AYT",
            topics = listOf("Sembolik Mantık ve Önermeler Mantığı Denetleme", "Çözümleyici Çizelge ile Geçerlilik ve Tutarlılık", "Niceleme (Yüklemler) Mantığı", "Klasik Mantık Kıyas Kuralları ve Şekilleri", "Bilimsel Yöntem ve Mantıksal Çıkarım")
        ),
        SubjectItem(
            id = "g12_sosyoloji", grade = 12, name = "Sosyoloji",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.SOCIOLOGY, accentHex = 0xFF8B5CF6, badgeTag = "AYT",
            topics = listOf("Küreselleşme ve Dijital Toplum", "Kentleşme, Göç ve Toplumsal Bütünleşme", "Kültürlenme, Kültürleşme ve Kültür Şoku", "Toplumsal Sapma ve Sosyal Kontrol", "Modern Sosyoloji Kuramları")
        ),
        SubjectItem(
            id = "g12_diksiyon", grade = 12, name = "Diksiyon ve Hitabet",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.COMMUNICATION_DICTION, accentHex = 0xFFBE123C,
            topics = listOf("Akademik Sunum ve Savunma Teknikleri", "Söylev (Nutuk) Türünün Özellikleri ve Analizi", "Ses Ekonomisi ve Dinleyici Etkileşimi", "Kültürlü Konuşma ve Kelime Seçimi", "Mülakat ve Kariyer İletişimi")
        ),
        SubjectItem(
            id = "g12_osmanlica", grade = 12, name = "Osmanlı Türkçesi",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.OTTOMAN_TURKISH, accentHex = 0xFF92400E,
            topics = listOf("Edebi ve Tarihi Matbu Metin Tahlili", "Rika ve Nesih Hatlarında Hızlı Okuma", "Tanzimat ve Milli Mücadele Dönemi Basın Metinleri", "Osmanlıca Diplomatika ve Sicil Örnekleri", "Karşılaştırmalı İmla ve Sözlük Kullanımı")
        ),
        SubjectItem(
            id = "g12_iletisim", grade = 12, name = "İletişim ve Sunum Becerileri",
            category = SubjectCategory.ELECTIVE, domain = SubjectDomain.COMMUNICATION_DICTION, accentHex = 0xFF0284C7,
            topics = listOf("Üniversite ve İş Hayatında İletişim Stratejileri", "İkna Psikolojisi ve Müzakere", "Kriz İletişimi ve Geri Bildirim Verme", "Profesyonel Portfolyo ve Proje Sunumu", "Kültürlerarası İletişim Yetkinliği")
        ),
        // Diller
        SubjectItem(
            id = "g12_ing_ydt", grade = 12, name = "İngilizce (YDT)",
            category = SubjectCategory.LANGUAGES, domain = SubjectDomain.ENGLISH, accentHex = 0xFF2563EB, badgeTag = "YKS / YDT",
            topics = listOf("Vocabulary, Phrasal Verbs & Prepositions", "Tenses, Modals, Passive & Causatives", "Clauses: Noun, Relative, Adverbial & Reductions", "Cloze Test, Sentence Completion & Restatement", "Reading Comprehension, Dialogue & Irrelevant Sentence")
        ),
        SubjectItem(
            id = "g12_alm", grade = 12, name = "Almanca",
            category = SubjectCategory.LANGUAGES, domain = SubjectDomain.GERMAN, accentHex = 0xFFD97706,
            topics = listOf("Studium, Karriere ve Bewerbung", "Wissenschaft, Technik ve Zukunft", "Passiv, Relativsätze ve Konjunktiv", "Leseverstehen (Akademik Okuma Parçaları)", "Kültürlerarası İletişim ve Çeviri")
        ),
        SubjectItem(
            id = "g12_arapca", grade = 12, name = "Arapça",
            category = SubjectCategory.LANGUAGES, domain = SubjectDomain.ARABIC, accentHex = 0xFF059669,
            topics = listOf("Modern Arapça Basın ve Akademik Metinler", "Hal, Temyiz ve Müstesna Yapıları", "Edebi Sanatlar ve Belagat Temelleri", "YDT Tarzı Arapça Paragraf ve Çeviri", "Dilbilgisi ve Bağlam Çözümleme")
        ),
        SubjectItem(
            id = "g12_turkdunyasi", grade = 12, name = "Türk Dünyası Dilleri",
            category = SubjectCategory.LANGUAGES, domain = SubjectDomain.TURKIC_LANGUAGES, accentHex = 0xFF0284C7,
            topics = listOf("Bağımsız Türk Cumhuriyetleri ve Dil Politikaları", "Ortak Türk Alfabesi ve Karşılaştırmalı Fonetik", "Türk Dünyası Klasik ve Modern Edebiyatı", "Çağdaş Basın Metinlerinden Çeviri", "Kültürel Entegrasyon ve Ortak Kavramlar")
        ),
        // Din & Ahlak
        SubjectItem(
            id = "g12_dkab", grade = 12, name = "Din Kültürü ve Ahlak Bilgisi",
            category = SubjectCategory.RELIGION, domain = SubjectDomain.RELIGION_ETHICS, accentHex = 0xFF047857, badgeTag = "YKS",
            topics = listOf("İslam ve Bilim (Medeniyetimizde Bilim Öncüleri)", "Anadolu'da İslam (Ahilik, Tasavvuf, Yesevilik, Mevlevilik)", "İslam Düşüncesinde Tasavvufi Yorumlar", "Güncel Dini Meseleler (Ekonomik, Tıbbi, Çevre Etiği)", "Hint ve Çin Dinleri (Hinduizm, Budizm, Konfüçyanizm)")
        ),
        SubjectItem(
            id = "g12_kuran", grade = 12, name = "Kur'an-ı Kerim",
            category = SubjectCategory.RELIGION, domain = SubjectDomain.QURAN, accentHex = 0xFF0F766E,
            topics = listOf("Kur'an'da Bilim, Akıl ve Evren Ayetleri", "Fetih ve Rahman Sureleri Mesajları", "İleri Kıraat, Tecvit ve Makam Bilgisi", "Kur'an'da Evrensel Ahlak İlkeleri", "Anlam Odaklı Kur'an Okuma")
        ),
        SubjectItem(
            id = "g12_siyer", grade = 12, name = "Peygamberimizin Hayatı",
            category = SubjectCategory.RELIGION, domain = SubjectDomain.PROPHET_LIFE, accentHex = 0xFF065F46,
            topics = listOf("Peygamberimizin Medeniyet İnşası ve Liderlik İlkeleri", "Evrensel Barış, Diplomasi ve İnsan Hakları", "Kırk Hadis ve Çağdaş Hayata Yansımaları", "İlim, Hikmet ve Eleştirel Düşünce Örnekleri", "Gençlik ve Toplumsal Sorumluluk Bilinci")
        )
    )

    val allSubjects: List<SubjectItem> by lazy {
        buildGrade5() + buildGrade6() + buildGrade7() + buildGrade8() +
            buildGrade9() + buildGrade10() + buildGrade11() + buildGrade12()
    }

    fun getSubjects(grade: Int, category: SubjectCategory? = null): List<SubjectItem> {
        return allSubjects.filter { it.grade == grade && (category == null || it.category == category) }
    }

    fun findSubjectById(subjectId: String): SubjectItem? =
        allSubjects.firstOrNull { it.id == subjectId }
}
