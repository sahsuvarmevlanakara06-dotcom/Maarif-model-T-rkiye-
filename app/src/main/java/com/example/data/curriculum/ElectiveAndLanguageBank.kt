package com.example.data.curriculum

import com.example.data.model.SubjectDomain

object ElectiveAndLanguageBank {

    fun factsFor(domain: SubjectDomain): List<ConceptFact> {
        return bank[domain] ?: defaultFacts
    }

    private val defaultFacts = listOf(
        ConceptFact(
            keyword = "genel",
            term = "Eleştirel ve Bilimsel Yaklaşım",
            definition = "Bilimsel ve disiplinli çalışma, veriye dayalı analiz, planlı tekrar ve aktif katılım gerektirir.",
            mcqPrompt = "Verimli öğrenme ve kalıcı başarı için aşağıdaki stratejilerden hangisi en etkilidir?",
            mcqCorrect = "Düzenli tekrar, aktif soru çözümü ve öz değerlendirme yapmak",
            mcqDistractors = listOf("Sadece sınav gecesi ezber yapmak", "Eksik konuları göz ardı etmek", "Plansız ve hedefsiz çalışmak"),
            blankBefore = "Yeni bilgilerin kalıcı hafızaya yerleşmesi için düzenli",
            blankAnswer = "tekrar",
            blankAfter = "yapmak büyük önem taşır.",
            tfStatement = "Kendi kendine soru sormak ve bilgi kartları kullanmak aktif öğrenmeyi güçlendirir.",
            tfIsTrue = true,
            wordBefore = "Türkiye Yüzyılı Maarif Modeli beceri temelli ve",
            wordCorrect = "bütüncül",
            wordWrong = "ezberci",
            wordAfter = "bir öğrenme yaklaşımını benimser.",
            openPrompt = "Bu derste öğrendiğiniz bilgilerin günlük yaşamınıza nasıl katkı sağladığını yazınız.",
            exampleUsage = "Bilgiyi problem çözme ve gerçek yaşam becerilerine dönüştürmek kalıcı öğrenme sağlar."
        )
    )

    private val bank: Map<SubjectDomain, List<ConceptFact>> = mapOf(
        SubjectDomain.RELIGION_ETHICS to listOf(
            ConceptFact(
                keyword = "kader",
                term = "Kader, Kaza ve Evrensel Yasalar",
                definition = "Kader, Allah'ın evrendeki her şeyi bir ölçü ve düzene göre planlaması; kaza ise bu planın zamanı gelince gerçekleşmesidir.",
                mcqPrompt = "Su 100°C'de kaynar, cisimler yer çekimiyle düşer gibi doğa kuralları hangi evrensel yasa kapsamındadır?",
                mcqCorrect = "Fiziksel Yasalar",
                mcqDistractors = listOf("Biyolojik Yasalar", "Toplumsal Yasalar", "Hukuki Yasalar"),
                blankBefore = "Bireyin elinden gelen tüm çabayı gösterdikten sonra sonucu Allah'a bırakmasına",
                blankAnswer = "tevekkül",
                blankAfter = "denir.",
                tfStatement = "İslam'da zekât ibadeti toplumsal dayanışmayı güçlendirir ve gelir adaletsizliğini azaltır.",
                tfIsTrue = true,
                wordBefore = "Allah'ın bir ve tek olduğuna inanma esasına",
                wordCorrect = "tevhid",
                wordWrong = "istişare",
                wordAfter = "denir.",
                openPrompt = "Doğru tevekkül anlayışı ile tembellik arasındaki farkı bir örnekle açıklayınız.",
                exampleUsage = "Çiftçinin tarlayı sürüp tohum ektikten sonra bereket dilemesi doğru tevekküldür."
            ),
            ConceptFact(
                keyword = "ahlak",
                term = "İslam Ahlakı, Değerler ve Bilgi Kaynakları",
                definition = "İslam'da bilgi kaynakları salim duyular, doğru haber ve selim akıldır; adalet, merhamet, dürüstlük ve kul hakkına riayet esastır.",
                mcqPrompt = "İslam düşüncesinde doğru bilginin üç temel kaynağı hangileridir?",
                mcqCorrect = "Selim akıl, salim duyular ve sadık (doğru) haber",
                mcqDistractors = listOf("Yalnızca rüyalar ve tahminler", "Sadece kişisel duygular", "Gelenek ve söylentiler"),
                blankBefore = "Allah'ı görüyormuş gibi bilinçli ve güzel davranma hâline Kur'an'da",
                blankAnswer = "ihsan",
                blankAfter = "denir.",
                tfStatement = "Anadolu'da kurulan Ahilik teşkilatı esnaf arasında dürüstlük, meslek ahlakı ve dayanışmayı yaygınlaştırmıştır.",
                tfIsTrue = true,
                wordBefore = "İslam'da can, mal, akıl, nesil ve dinin korunması",
                wordCorrect = "beş temel esas",
                wordWrong = "sadece örfi kurallar",
                wordAfter = "olarak kabul edilir.",
                openPrompt = "Ahilik kültürünün ticaret ahlakı ve toplumsal dayanışmaya katkılarını açıklayınız.",
                exampleUsage = "Kaliteli üretim, dürüst ticaret ve çırak-usta eğitimiyle toplumsal güveni güçlendirmiştir."
            )
        ),

        SubjectDomain.QURAN to listOf(
            ConceptFact(
                keyword = "tecvit",
                term = "Tecvit ve Kur'an Okuma Adabı",
                definition = "Tecvit, Kur'an harflerini mahreçlerine ve sıfatlarına uygun, uzatma ve durak kurallarına dikkat ederek güzel okuma ilmidir.",
                mcqPrompt = "Kur'an-ı Kerim okurken harflerin çıkış yerlerine ve kurallarına uygun olarak güzel okuma ilmine ne ad verilir?",
                mcqCorrect = "Tecvit",
                mcqDistractors = listOf("Fıkıh", "Kelam", "Siyer"),
                blankBefore = "Kur'an'da durulacak ve yeniden başlanacak yerleri gösteren işaretlere",
                blankAnswer = "secavend",
                blankAfter = "(vakıf işaretleri) denir.",
                tfStatement = "Med harfleri (elif, vav, ye) kendilerinden önceki harfin sesini uzatarak okunmasını sağlar.",
                tfIsTrue = true,
                wordBefore = "Kur'an-ı Kerim'in ayetlerini açıklamalı ve derinlemesine yorumlayan ilim dalına",
                wordCorrect = "tefsir",
                wordWrong = "hadis",
                wordAfter = "denir.",
                openPrompt = "Kur'an-ı Kerim'i yalnızca yüzünden okumakla kalmayıp anlamını (mealini) öğrenmenin önemini açıklayınız.",
                exampleUsage = "Evrensel ahlak, tefekkür ve sorumluluk mesajlarını hayata yansıtmamızı sağlar."
            )
        ),

        SubjectDomain.PROPHET_LIFE to listOf(
            ConceptFact(
                keyword = "siyer",
                term = "Hz. Muhammed'in (s.a.v.) Örnek Hayatı",
                definition = "Hz. Muhammed güvenilirliğiyle 'El-Emin' olarak anılmış, Medine Sözleşmesi ve Veda Hutbesi ile adalet ve insan haklarını vurgulamıştır.",
                mcqPrompt = "Peygamberimizin gençliğinde Mekke'de haksızlığa uğrayanları korumak amacıyla katıldığı erdemliler topluluğunun adı nedir?",
                mcqCorrect = "Hılfu'l-Fudul",
                mcqDistractors = listOf("Darü'n-Nedve", "Suffe", "Akabe"),
                blankBefore = "Hz. Muhammed'in (s.a.v.) Mekke'den Medine'ye gerçekleştirdiği tarihi göçe",
                blankAnswer = "Hicret",
                blankAfter = "denir.",
                tfStatement = "Veda Hutbesi'nde insanlar arasında üstünlüğün ırk veya renkte değil, yalnızca takva ve güzel ahlakta olduğu vurgulanmıştır.",
                tfIsTrue = true,
                wordBefore = "Peygamberimizin önemli kararları almadan önce arkadaşlarının görüşlerine başvurmasına",
                wordCorrect = "istişare",
                wordWrong = "muhacir",
                wordAfter = "denir.",
                openPrompt = "Peygamberimizin 'El-Emin' (güvenilir) sıfatının günümüz gençleri için taşıdığı anlamı açıklayınız.",
                exampleUsage = "Doğruluk, sözünde durma ve emanete sahip çıkma toplumsal güvenin temelidir."
            )
        ),

        SubjectDomain.ISLAMIC_CULTURE to listOf(
            ConceptFact(
                keyword = "medeniyet",
                term = "İslam Kültür ve Medeniyetinde Bilim ve Sanat",
                definition = "Beytülhikme, Nizamiye Medreseleri, rasathaneler ve vakıflar; İbn Sina, Harezmi, Biruni gibi öncü bilim insanlarını yetiştirmiştir.",
                mcqPrompt = "Tıp alanındaki 'El-Kanun fi't-Tıb' eseri yüzyıllarca Avrupa üniversitelerinde okutulan İslam bilim insanı kimdir?",
                mcqCorrect = "İbn Sina",
                mcqDistractors = listOf("Piri Reis", "Mimar Sinan", "Evliya Çelebi"),
                blankBefore = "Cebir ilminin kurucusu sayılan ve algoritma kavramına adını veren büyük matematikçi",
                blankAnswer = "Harezmi",
                blankAfter = "olarak bilinir.",
                tfStatement = "Bağdat'ta kurulan Beytülhikme, çeviri ve bilimsel araştırmaların yapıldığı önemli bir bilim merkeziydi.",
                tfIsTrue = true,
                wordBefore = "İslam sanatında güzel yazı yazma sanatına",
                wordCorrect = "hat",
                wordWrong = "minyatür",
                wordAfter = "sanatı adı verilir.",
                openPrompt = "İslam medeniyetinde vakıf kurumlarının eğitim, sağlık ve şehir yaşamına katkılarını açıklayınız.",
                exampleUsage = "Medrese, darüşşifa, kütüphane ve imarethaneler vakıf gelirleriyle halka ücretsiz hizmet sunardı."
            )
        ),

        SubjectDomain.ENGLISH to listOf(
            ConceptFact(
                keyword = "grammar",
                term = "English Tenses, Modals & Clauses",
                definition = "Correct tense usage, modal verbs, conditionals, and connectors enable clear communication from A1 to YDT level.",
                mcqPrompt = "Choose the grammatically correct sentence: 'If it ____ tomorrow, we will cancel the picnic.'",
                mcqCorrect = "rains",
                mcqDistractors = listOf("will rain", "rained", "raining"),
                blankBefore = "In English, 'to count on someone' means to",
                blankAnswer = "trust",
                blankAfter = "or rely on a close friend.",
                tfStatement = "In Type 1 Conditional sentences, we use Present Simple in the 'if-clause' and 'will + V1' in the main clause.",
                tfIsTrue = true,
                wordBefore = "She has lived in Ankara",
                wordCorrect = "since",
                wordWrong = "for",
                wordAfter = "2019 and loves the city.",
                openPrompt = "Write two English sentences describing your daily study routine or future career goal.",
                exampleUsage = "Example: I study English every day because I want to become a software engineer."
            ),
            ConceptFact(
                keyword = "vocab",
                term = "Academic Vocabulary & Connectors",
                definition = "Connectors like 'although', 'however', 'therefore', and 'despite' show contrast or cause-effect in reading passages.",
                mcqPrompt = "Which connector correctly completes the contrast sentence? '____ it was raining heavily, the students arrived on time.'",
                mcqCorrect = "Although",
                mcqDistractors = listOf("Because", "Therefore", "Despite of"),
                blankBefore = "The passive form of 'Scientists discovered a new planet' is: 'A new planet was",
                blankAnswer = "discovered",
                blankAfter = "by scientists.'",
                tfStatement = "The phrasal verb 'give up' means to stop trying or quit doing something.",
                tfIsTrue = true,
                wordBefore = "We couldn't go out",
                wordCorrect = "because of",
                wordWrong = "because",
                wordAfter = "the heavy snowstorm.",
                openPrompt = "Explain the difference between 'although' and 'despite' in English with a short example.",
                exampleUsage = "'Although' is followed by a full clause (S+V); 'despite' is followed by a noun/gerund."
            )
        ),

        SubjectDomain.GERMAN to listOf(
            ConceptFact(
                keyword = "deutsch",
                term = "Deutsch: Artikel, Verben & Alltag",
                definition = "Almancada isimlerin üç artikeli vardır (der, die, das) ve düz cümlede çekimli fiil her zaman 2. sırada bulunur.",
                mcqPrompt = "'Ich ____ aus der Türkei und lerne Deutsch.' cümlesinde boşluğa hangi fiil çekimi gelmelidir?",
                mcqCorrect = "komme",
                mcqDistractors = listOf("kommst", "kommt", "kommen"),
                blankBefore = "Almancada 'Günaydın' anlamına gelen selamlaşma ifadesi 'Guten",
                blankAnswer = "Morgen",
                blankAfter = "' şeklinde yazılır.",
                tfStatement = "Almancada tüm isimlerin (Substantive) ilk harfi cümle ortasında olsa bile büyük yazılır.",
                tfIsTrue = true,
                wordBefore = "'Wie ____ du?' sorusu karşımızdakinin adını sormak için kullanılır ve boşluğa",
                wordCorrect = "heißt",
                wordWrong = "heißen",
                wordAfter = "getirilir.",
                openPrompt = "Almanca kendinizi tanıtan (adınız, yaşınız, yaşadığınız şehir) 2 kısa cümle yazınız.",
                exampleUsage = "Ich heiße Emre. Ich bin 14 Jahre alt und wohne in İstanbul."
            )
        ),

        SubjectDomain.ARABIC to listOf(
            ConceptFact(
                keyword = "arapca",
                term = "Arapça Temel Yapılar ve İsim/Fiil Cümlesi",
                definition = "Arapça sağdan sola yazılır; isimle başlayan cümleye İsim Cümlesi (Mübteda + Haber), fiille başlayan cümleye Fiil Cümlesi denir.",
                mcqPrompt = "Arapçada 'Merhaba / Hoş geldiniz' anlamında yaygın kullanılan ifade hangisidir?",
                mcqCorrect = "Ehlen ve sehlen (أهلاً وسهلاً)",
                mcqDistractors = listOf("İle'l-lika (Güle güle)", "Şükran (Teşekkürler)", "Afven (Rica ederim)"),
                blankBefore = "Arapçada isimle başlayan cümlelerin özne görevindeki ilk ögesine",
                blankAnswer = "mübteda",
                blankAfter = "denir.",
                tfStatement = "Arapçada 'Kitabun' kelimesinin başına 'El-' (ال) takısı gelirse kelime belirli (marife) hâle gelir.",
                tfIsTrue = true,
                wordBefore = "Arapçada geçmiş zamanı ifade eden fiil kipine",
                wordCorrect = "mazi",
                wordWrong = "muzari",
                wordAfter = "fiil adı verilir.",
                openPrompt = "Arapçada isim cümlesi ile fiil cümlesi arasındaki temel diziliş farkını açıklayınız.",
                exampleUsage = "İsim cümlesi isimle (Mübteda+Haber), fiil cümlesi fiille (Fiil+Fail+Meful) başlar."
            )
        ),

        SubjectDomain.TURKIC_LANGUAGES to listOf(
            ConceptFact(
                keyword = "turkdunyasi",
                term = "Türk Dünyası Dilleri ve Ortak Kökler",
                definition = "Azerbaycan, Türkmen (Oğuz), Kazak, Kırgız (Kıpçak) ve Özbek (Karluk) Türkçeleri aynı ortak kök ve ses uyumu mirasını paylaşır.",
                mcqPrompt = "Türkiye Türkçesi, Azerbaycan Türkçesi ve Türkmence, Türk lehçelerinin hangi ana grubunda yer alır?",
                mcqCorrect = "Oğuz (Güneybatı) Grubu",
                mcqDistractors = listOf("Kıpçak (Kuzeybatı) Grubu", "Karluk (Güneydoğu) Grubu", "Sibirya Grubu"),
                blankBefore = "Kırgız halkının dünyaca ünlü en uzun destanının adı",
                blankAnswer = "Manas",
                blankAfter = "Destanı'dır.",
                tfStatement = "Azerbaycan Türkçesinde 'Məktəb' sözcüğü okul, 'Kitabxana' sözcüğü kütüphane anlamına gelir.",
                tfIsTrue = true,
                wordBefore = "Kazakça ve Kırgızca, Türk dilleri sınıflandırmasında",
                wordCorrect = "Kıpçak",
                wordWrong = "Oğuz",
                wordAfter = "grubunda yer alır.",
                openPrompt = "Türk dünyası lehçeleri arasındaki ortak kelimelerin kültürel bağlar açısından önemini açıklayınız.",
                exampleUsage = "Ana, su, göz, el, güneş, kitap gibi binlerce ortak kök iletişim ve kardeşlik köprüsü kurar."
            )
        ),

        SubjectDomain.OTTOMAN_TURKISH to listOf(
            ConceptFact(
                keyword = "osmanlica",
                term = "Osmanlı Türkçesi Okutucular ve Hatlar",
                definition = "Osmanlı Türkçesinde Türkçe kelimeleri okumayı sağlayan Elif (a/e), Vav (o/ö/u/ü), Ye (ı/i) ve He (a/e) harflerine okutucu denir.",
                mcqPrompt = "Osmanlı Türkçesinde Türkçe kelimelerde 'ı, i' seslerini göstermek için kullanılan okutucu harf hangisidir?",
                mcqCorrect = "Ye (ي)",
                mcqDistractors = listOf("Vav (و)", "Mim (م)", "Sin (س)"),
                blankBefore = "Osmanlı arşiv belgelerinde ve günlük yazışmalarda hızlı yazım için en çok kullanılan el yazısı türü",
                blankAnswer = "Rika",
                blankAfter = "hattıdır.",
                tfStatement = "Osmanlı Türkçesi yeni bir dil değil, Türkçenin tarihî bir döneminde Arap alfabesiyle yazılmış biçimidir.",
                tfIsTrue = true,
                wordBefore = "Osmanlı Türkçesinde 'o, ö, u, ü' seslerini karşılayan okutucu harf",
                wordCorrect = "Vav",
                wordWrong = "Elif",
                wordAfter = "harfidir.",
                openPrompt = "Osmanlı Türkçesi bilmenin tarih ve edebiyat araştırmalarına nasıl bir katkı sağladığını açıklayınız.",
                exampleUsage = "Arşiv belgelerini, kitabeleri ve klasik edebiyat eserlerini birinci elden okuyabilmemizi sağlar."
            )
        ),

        SubjectDomain.COMPUTER_SCIENCE to listOf(
            ConceptFact(
                keyword = "algoritma",
                term = "Algoritma, Kodlama ve Dijital Güvenlik",
                definition = "Algoritma bir problemi çözmek için izlenen sonlu, kesin ve sıralı adımlardır; bilgisayarlar verileri 0 ve 1 (ikili sistem) ile işler.",
                mcqPrompt = "Bir problemi çözmek için başlangıcı ve bitişi belli olan sıralı işlem adımlarına ne ad verilir?",
                mcqCorrect = "Algoritma",
                mcqDistractors = listOf("Donanım sürücüsü", "Piksel", "Önbellek"),
                blankBefore = "Bilgisayar sistemlerinin verileri yalnızca 0 ve 1 rakamlarıyla temsil ettiği sayı sistemine",
                blankAnswer = "ikili",
                blankAfter = "(binary) sayı sistemi denir.",
                tfStatement = "Programlamada belirli bir koşul sağlandığı sürece aynı kod bloğunu tekrarlayan yapılara döngü (loop) denir.",
                tfIsTrue = true,
                wordBefore = "Güçlü bir parola büyük-küçük harf, rakam ve özel karakter içermeli; kişisel doğum tarihi gibi kolay tahmin edilir",
                wordCorrect = "olmamalıdır",
                wordWrong = "olmalıdır",
                wordAfter = ".",
                openPrompt = "Günlük hayattan bir işin (örneğin çay demleme veya ödev planlama) 4 adımlık basit bir algoritmasını yazınız.",
                exampleUsage = "1. Başla -> 2. Malzemeleri hazırla -> 3. İşlemi uygula -> 4. Sonucu kontrol et ve Bitir."
            )
        ),

        SubjectDomain.TECH_DESIGN to listOf(
            ConceptFact(
                keyword = "tasarım",
                term = "Tasarım Odaklı Düşünme, Ergonomi ve Biyomimikri",
                definition = "İnsan vücuduna uyumlu ürün tasarımına ergonomi, doğadaki canlılardan ilham alarak teknoloji üretmeye biyomimikri denir.",
                mcqPrompt = "Yalıçapkını kuşunun gagasından ilham alınarak hızlı tren burnunun tasarlanması hangi kavrama örnektir?",
                mcqCorrect = "Biyomimikri",
                mcqDistractors = listOf("Telif hakkı", "Seri üretim", "Standartlaştırma"),
                blankBefore = "Ürünlerin insan anatomisine ve kullanım konforuna en uygun biçimde tasarlanmasını inceleyen bilim dalına",
                blankAnswer = "ergonomi",
                blankAfter = "denir.",
                tfStatement = "Yeni bir buluşun yasal olarak mucidine ait olduğunu koruyan belgeye patent adı verilir.",
                tfIsTrue = true,
                wordBefore = "Bir fikrin seri üretime geçmeden önce test etmek amacıyla hazırlanan ilk çalışan örneğine",
                wordCorrect = "prototip",
                wordWrong = "logo",
                wordAfter = "denir.",
                openPrompt = "Okul çantanızı veya çalışma masanızı daha ergonomik hâle getirmek için nasıl bir tasarım önerirsiniz?",
                exampleUsage = "Ağırlığı omuzlara eşit dağıtan destekli askılar ve boya göre ayarlanabilir masa yüksekliği."
            )
        ),

        SubjectDomain.ARTS_MUSIC to listOf(
            ConceptFact(
                keyword = "sanat",
                term = "Görsel Sanatlar ve Müzik Temelleri",
                definition = "Kırmızı, sarı ve mavi ana renklerdir; müzikte ise porte (dizek) beş çizgi dört aralıktan oluşur ve ritim-melodi bütünlüğü sağlar.",
                mcqPrompt = "Resim sanatında kırmızı, sarı ve mavi renklerin oluşturduğu temel grup hangisidir?",
                mcqCorrect = "Ana renkler",
                mcqDistractors = listOf("Ara renkler", "Nötr renkler", "Soğuk renkler"),
                blankBefore = "Müzik notalarının üzerine yazıldığı 5 yatay çizgi ve 4 aralıktan oluşan sisteme",
                blankAnswer = "porte",
                blankAfter = "(dizek) denir.",
                tfStatement = "Perspektif tekniği, iki boyutlu bir yüzeyde derinlik ve uzaklık algısı oluşturmak için kullanılır.",
                tfIsTrue = true,
                wordBefore = "Su yüzeyine serpilmiş boyalarla desen oluşturup kâğıda aktarılan geleneksel Türk sanatı",
                wordCorrect = "ebru",
                wordWrong = "fresk",
                wordAfter = "olarak adlandırılır.",
                openPrompt = "Sanatın ve müziğin bireyin yaratıcılığı ile duygusal gelişimine olan katkısını kendi cümlelerinizle açıklayınız.",
                exampleUsage = "Estetik duyarlılığı artırır, stres yönetimini destekler ve kültürel mirası yaşatır."
            )
        ),

        SubjectDomain.PHYSICAL_ED to listOf(
            ConceptFact(
                keyword = "spor",
                term = "Sağlıklı Yaşam, Hareket ve Fair-Play",
                definition = "Egzersiz öncesi ısınma sakatlanmaları önler; Fair-Play ise kurallara saygılı, dürüst ve centilmence yarışma kültürüdür.",
                mcqPrompt = "Spor müsabakalarında kurallara saygı göstermeyi, dürüstlüğü ve rakibe nezaketi ifade eden kavram hangisidir?",
                mcqCorrect = "Fair-Play (Adil Oyun)",
                mcqDistractors = listOf("Oksijen borçlanması", "Doping", "Statik yüklenme"),
                blankBefore = "Antrenman öncesinde kasları ve eklemleri egzersize hazırlayarak sakatlanma riskini azaltan aşamaya",
                blankAnswer = "ısınma",
                blankAfter = "denir.",
                tfStatement = "Düzenli fiziksel aktivite hem kalp-damar sağlığını güçlendirir hem de derslerde odaklanmayı artırır.",
                tfIsTrue = true,
                wordBefore = "Egzersiz sonrasında kalp atış hızını kademeli olarak normale döndürmek için",
                wordCorrect = "soğuma ve esneme",
                wordWrong = "ani durma",
                wordAfter = "egzersizleri yapılmalıdır.",
                openPrompt = "Takım sporlarının iş birliği, iletişim ve liderlik becerilerine nasıl katkı sağladığını yazınız.",
                exampleUsage = "Ortak hedef için sorumluluk paylaşmayı ve kazanmayı/kaybetmeyi olgunlukla karşılamayı öğretir."
            )
        ),

        SubjectDomain.WRITING_DRAMA to listOf(
            ConceptFact(
                keyword = "drama",
                term = "Yaratıcı Yazma ve Eğitimde Drama",
                definition = "Yaratıcı yazarlık güçlü kurgu ve betimleme gerektirirken; drama rol oynama, doğaçlama ve beden diliyle empati geliştirir.",
                mcqPrompt = "Önceden yazılmış bir metne bağlı kalmadan anlık yaratıcılıkla canlandırma yapma tekniğine ne denir?",
                mcqCorrect = "Doğaçlama",
                mcqDistractors = listOf("Dipnot", "Monografi", "Özetleme"),
                blankBefore = "Hiç konuşmadan yalnızca yüz ifadeleri (mimik) ve beden hareketleriyle hikâye anlatma sanatına",
                blankAnswer = "pandomim",
                blankAfter = "denir.",
                tfStatement = "Bir hikâyenin dört temel unsuru olay, yer, zaman ve kişi kadrosudur.",
                tfIsTrue = true,
                wordBefore = "Etkili bir yazı taslağı oluştururken düşüncelerin mantıklı sırayla akması",
                wordCorrect = "tutarlılık",
                wordWrong = "tekrar",
                wordAfter = "ilkesiyle sağlanır.",
                openPrompt = "Drama etkinliklerinde rol oynamanın empati becerimizi neden güçlendirdiğini açıklayınız.",
                exampleUsage = "Farklı karakterlerin gözünden olaylara bakarak onların duygu ve gereksinimlerini anlamamızı sağlar."
            )
        ),

        SubjectDomain.MEDIA_LAW_DEMOCRACY to listOf(
            ConceptFact(
                keyword = "medya",
                term = "Medya Okuryazarlığı, Hukuk ve Demokrasi",
                definition = "Eleştirel medya okuryazarlığı haber kaynaklarını doğrular; hukuk devleti ise temel hakları ve kuvvetler ayrılığını güvence altına alır.",
                mcqPrompt = "Kamuoyunu yanıltmak amacıyla kasıtlı olarak üretilen yanlış veya çarpıtılmış bilgiye ne ad verilir?",
                mcqCorrect = "Dezenformasyon",
                mcqDistractors = listOf("Biyografi", "İnfografik", "Arabuluculuk"),
                blankBefore = "Demokratik hukuk devletlerinde yasama, yürütme ve yargı yetkilerinin farklı organlarda toplanmasına",
                blankAnswer = "kuvvetler ayrılığı",
                blankAfter = "denir.",
                tfStatement = "Sosyal medyada karşılaşılan bir haberi paylaşmadan önce birden fazla güvenilir kaynaktan doğrulamak gerekir.",
                tfIsTrue = true,
                wordBefore = "İki taraf arasındaki anlaşmazlığın mahkemeye gitmeden tarafsız bir uzman eşliğinde çözülmesine",
                wordCorrect = "arabuluculuk",
                wordWrong = "tek taraflı yaptırım",
                wordAfter = "denir.",
                openPrompt = "İnternette gördüğünüz bir haberin doğruluğunu kontrol etmek için hangi adımları izlersiniz?",
                exampleUsage = "Resmi kaynakları kontrol etmek, tarihini incelemek ve farklı güvenilir haber ajanslarıyla karşılaştırmak."
            )
        ),

        SubjectDomain.HEALTH_GUIDANCE to listOf(
            ConceptFact(
                keyword = "saglik",
                term = "Sağlık, Trafik Kültürü ve Kariyer Rehberliği",
                definition = "İlk yardımda temel kural zarar vermemek ve 112'yi aramaktır; kariyer planlamada ise ilgi, yetenek ve değerlerin uyumu esastır.",
                mcqPrompt = "Verimli ders çalışmada 25 dakika odaklanıp 5 dakika kısa mola vermeye dayanan zaman yönetimi tekniği hangisidir?",
                mcqCorrect = "Pomodoro Tekniği",
                mcqDistractors = listOf("Gelişigüzel tekrar", "Aralıksız maraton", "Pasif dinleme"),
                blankBefore = "Acil sağlık, polis ve itfaiye durumlarında Türkiye'de tek numara olarak aranan acil çağrı hattı",
                blankAnswer = "112",
                blankAfter = "numarasıdır.",
                tfStatement = "Kariyer ve meslek seçiminde bireyin kendi ilgi, yetenek ve kişilik özelliklerini tanıması belirleyicidir.",
                tfIsTrue = true,
                wordBefore = "Trafikte yaya geçitlerinde geçiş önceliği her zaman",
                wordCorrect = "yayalara",
                wordWrong = "hızlı araçlara",
                wordAfter = "aittir.",
                openPrompt = "Sınav kaygısını sağlıklı bir düzeyde tutmak için uygulayabileceğiniz iki yöntemi açıklayınız.",
                exampleUsage = "Planlı çalışmak, diyafram nefesi almak, yeterli uyumak ve gerçekçi hedefler koymak."
            )
        ),

        SubjectDomain.ASTRONOMY to listOf(
            ConceptFact(
                keyword = "uzay",
                term = "Astronomi, Kepler Yasaları ve Evren",
                definition = "Gezegenler Güneş etrafında eliptik yörüngelerde dolanır; ışık yılı bir zaman değil, ışığın bir yılda aldığı mesafe birimidir.",
                mcqPrompt = "Astronomide yıldızlar arası çok büyük mesafeleri ölçmek için kullanılan 'ışık yılı' neyin birimidir?",
                mcqCorrect = "Uzunluk (Mesafe)",
                mcqDistractors = listOf("Zaman", "Kütle", "Sıcaklık"),
                blankBefore = "Gezegenlerin Güneş etrafındaki yörüngelerinin elips şeklinde olduğunu ortaya koyan bilim insanı",
                blankAnswer = "Kepler",
                blankAfter = "olarak bilinir.",
                tfStatement = "Güneş sistemindeki gezegenler Güneş'e yaklaştıkça yörünge hızları artar.",
                tfIsTrue = true,
                wordBefore = "Büyük kütleli bir yıldızın ömrünün sonunda şiddetli patlamayla dış katmanlarını savurmasına",
                wordCorrect = "süpernova",
                wordWrong = "tutulma",
                wordAfter = "denir.",
                openPrompt = "Işık kirliliğinin astronomi gözlemlerini nasıl etkilediğini ve gözlemevlerinin neden dağ tepelerine kurulduğunu açıklayınız.",
                exampleUsage = "Şehir ışıkları gökyüzünü parlatarak sönük yıldızları gizler; yüksek ve kuru bölgeler net görüş sağlar."
            )
        ),

        SubjectDomain.COMMUNICATION_DICTION to listOf(
            ConceptFact(
                keyword = "hitabet",
                term = "Diksiyon, Retorik ve Etkili Sunum",
                definition = "Doğru diyafram nefesi, net boğumlama (artikülasyon), göz teması ve Aristoteles'in Ethos-Pathos-Logos ilkeleri ikna gücünü artırır.",
                mcqPrompt = "Konuşmacının kelimeleri harf yutmadan net ve anlaşılır biçimde seslendirmesine diksiyonda ne ad verilir?",
                mcqCorrect = "Boğumlama (Artikülasyon)",
                mcqDistractors = listOf("Monotonluk", "İç konuşma", "Pasif dinleme"),
                blankBefore = "Topluluk önünde uzun ve güçlü konuşabilmek için göğüs yerine",
                blankAnswer = "diyafram",
                blankAfter = "nefesi kullanılmalıdır.",
                tfStatement = "Etkili bir sunumda slaytlar uzun paragraflarla doldurulmamalı, anahtar kavramlar ve görseller öne çıkarılmalıdır.",
                tfIsTrue = true,
                wordBefore = "Aristoteles'in retorik üçgeninde mantıksal kanıtlara ve verilere dayalı ikna unsuruna",
                wordCorrect = "Logos",
                wordWrong = "Pathos",
                wordAfter = "denir.",
                openPrompt = "Topluluk önünde konuşurken dinleyicinin ilgisini canlı tutmak için dikkat edeceğiniz üç kuralı yazınız.",
                exampleUsage = "Göz teması kurmak, ses tonunu vurgulamak, doğal beden dili kullanmak ve akıcı örnekler vermek."
            )
        ),

        SubjectDomain.FOREIGN_LIT to listOf(
            ConceptFact(
                keyword = "flit",
                term = "World Literature & Critical Analysis",
                definition = "Literary analysis examines plot, characterization, setting, point of view, symbolism, and historical context.",
                mcqPrompt = "In literature, a sonnet traditionally consists of how many lines?",
                mcqCorrect = "14 lines",
                mcqDistractors = listOf("8 lines", "12 lines", "20 lines"),
                blankBefore = "William Shakespeare's famous tragedy 'Hamlet' explores themes of revenge, doubt, and",
                blankAnswer = "justice",
                blankAfter = "in the Danish royal court.",
                tfStatement = "A metaphor makes an implicit comparison between two unrelated things without using 'like' or 'as'.",
                tfIsTrue = true,
                wordBefore = "The central character around whom the main conflict of a novel revolves is called the",
                wordCorrect = "protagonist",
                wordWrong = "antagonist",
                wordAfter = ".",
                openPrompt = "Explain the difference between a simile and a metaphor in English literature with one example.",
                exampleUsage = "Simile uses 'like/as' (brave as a lion); metaphor states directly (he is a lion)."
            )
        )
    )
}
