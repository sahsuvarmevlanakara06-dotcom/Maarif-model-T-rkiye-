package com.example.data.curriculum

import com.example.data.model.QuestionFormat
import com.example.data.model.QuizQuestion
import com.example.data.model.SubjectDomain
import com.example.data.model.SubjectItem
import kotlin.math.abs
import kotlin.math.pow
import kotlin.random.Random

/**
 * Generates infinite randomized, mathematically valid numerical and quantitative questions
 * tailored to grade (5-12), domain (Math, Physics, Chemistry, Mind Games, Economy),
 * selected topic, and any of the 6 question formats.
 */
object MathProblemGenerator {

    private data class ComputedProblem(
        val topic: String,
        val questionText: String,
        val correctAnswer: String,
        val distractors: List<String>,
        val explanation: String,
        val conceptTerm: String,
        val conceptDef: String,
        val blankBefore: String,
        val blankAfter: String,
        val tfStatement: String,
        val tfIsTrue: Boolean,
        val wordBefore: String,
        val wordCorrect: String,
        val wordWrong: String,
        val wordAfter: String,
        val openPrompt: String,
        val openHint: String
    )

    fun canGenerateQuantitative(domain: SubjectDomain): Boolean {
        return domain == SubjectDomain.MATH ||
            domain == SubjectDomain.PHYSICS ||
            domain == SubjectDomain.CHEMISTRY ||
            domain == SubjectDomain.MIND_GAMES ||
            domain == SubjectDomain.ECONOMY_ENTREPRENEURSHIP
    }

    fun generateQuestion(
        subject: SubjectItem,
        topic: String,
        format: QuestionFormat,
        index: Int,
        rng: Random,
        examModeLabel: String? = null
    ): QuizQuestion {
        val problem = when (subject.domain) {
            SubjectDomain.MATH -> generateMathProblem(subject.grade, topic, rng)
            SubjectDomain.PHYSICS -> generatePhysicsProblem(subject.grade, topic, rng)
            SubjectDomain.CHEMISTRY -> generateChemistryProblem(subject.grade, topic, rng)
            SubjectDomain.MIND_GAMES -> generateMindGameProblem(topic, rng)
            SubjectDomain.ECONOMY_ENTREPRENEURSHIP -> generateEconomyProblem(topic, rng)
            else -> generateMathProblem(subject.grade, topic, rng)
        }
        return wrapInFormat(
            p = problem,
            format = format,
            id = "dyn_${subject.id}_${index}_${rng.nextInt(100000)}",
            rng = rng,
            examModeLabel = examModeLabel
        )
    }

    private fun gcd(a: Int, b: Int): Int {
        var x = abs(a)
        var y = abs(b)
        while (y != 0) {
            val t = x % y
            x = y
            y = t
        }
        return if (x == 0) 1 else x
    }

    private fun lcm(a: Int, b: Int): Int = abs(a * b) / gcd(a, b)

    private fun generateMathProblem(grade: Int, topic: String, rng: Random): ComputedProblem {
        val lower = topic.lowercase()
        return when {
            lower.contains("üslü") -> {
                val base = rng.nextInt(2, 6)
                val exp1 = rng.nextInt(2, 5)
                val exp2 = rng.nextInt(1, 4)
                val useMultiply = rng.nextBoolean()
                if (useMultiply) {
                    val sumExp = exp1 + exp2
                    val ans = "$base^$sumExp"
                    ComputedProblem(
                        topic = topic,
                        questionText = "$base^$exp1 × $base^$exp2 işleminin üslü gösterimi aşağıdakilerden hangisidir?",
                        correctAnswer = ans,
                        distractors = listOf("$base^${exp1 * exp2}", "${base * base}^$sumExp", "$base^${abs(exp1 - exp2) + 1}"),
                        explanation = "Tabanları eşit olan üslü ifadeler çarpılırken taban aynen yazılır, üsler toplanır: $exp1 + $exp2 = $sumExp.",
                        conceptTerm = "Üslü İfadelerde Çarpma",
                        conceptDef = "Tabanları aynı üslü ifadelerin çarpımında üsler toplanır: a^m · a^n = a^(m+n).",
                        blankBefore = "Tabanları aynı olan $base^$exp1 ve $base^$exp2 ifadelerinin çarpımı",
                        blankAfter = "şeklinde yazılır.",
                        tfStatement = if (rng.nextBoolean()) "$base^$exp1 × $base^$exp2 = $base^$sumExp eşitliği doğrudur." else "$base^$exp1 × $base^$exp2 = $base^${exp1 * exp2} eşitliği doğrudur.",
                        tfIsTrue = true, // normalized below
                        wordBefore = "Tabanları eşit üslü ifadeler çarpılırken üsler",
                        wordCorrect = "toplanır",
                        wordWrong = "çarpılır",
                        wordAfter = "ve ortak taban aynen yazılır.",
                        openPrompt = "$base^$exp1 × $base^$exp2 işleminin sonucunu kuralını açıklayarak yazınız.",
                        openHint = "Tabanlar eşitken üslere ne yapılır?"
                    ).let { p ->
                        val isTrue = rng.nextBoolean()
                        p.copy(
                            tfStatement = if (isTrue) "$base^$exp1 × $base^$exp2 işleminin sonucu $base^$sumExp olur." else "$base^$exp1 × $base^$exp2 işleminin sonucu $base^${exp1 * exp2} olur.",
                            tfIsTrue = isTrue
                        )
                    }
                } else {
                    val valResult = base.toDouble().pow(exp1.toDouble()).toInt()
                    ComputedProblem(
                        topic = topic,
                        questionText = "$base^$exp1 üslü ifadesinin sayısal değeri kaçtır?",
                        correctAnswer = "$valResult",
                        distractors = listOf("${base * exp1}", "${valResult + base}", "${valResult * base}").distinct().take(3),
                        explanation = "$base^$exp1 ifadesi, $base sayısının $exp1 kez kendisiyle çarpılmasıdır = $valResult.",
                        conceptTerm = "Üslü İfade Değeri",
                        conceptDef = "a^n ifadesi, taban olan a sayısının n defa kendisiyle çarpılması anlamına gelir.",
                        blankBefore = "$base^$exp1 üslü ifadesinin değeri",
                        blankAfter = "sayısına eşittir.",
                        tfStatement = "$base^$exp1 ifadesinin değeri $valResult sayısına eşittir.",
                        tfIsTrue = true,
                        wordBefore = "Bir sayının sıfırıncı kuvveti (sıfır hariç) her zaman",
                        wordCorrect = "1 (bir)",
                        wordWrong = "0 (sıfır)",
                        wordAfter = "değerine eşittir.",
                        openPrompt = "$base^$exp1 ifadesinin nasıl hesaplandığını kendi cümlelerinizle açıklayınız.",
                        openHint = "Taban ve kuvvet (üs) kavramlarını kullan."
                    )
                }
            }

            lower.contains("ebob") || lower.contains("ekok") || lower.contains("çarpan") -> {
                val factor = listOf(4, 6, 8, 9, 12).random(rng)
                val m1 = listOf(2, 3, 5).random(rng)
                val m2 = listOf(3, 4, 5, 7).filter { gcd(it, m1) == 1 }.random(rng)
                val a = factor * m1
                val b = factor * m2
                val askEbob = rng.nextBoolean()
                val ans = if (askEbob) gcd(a, b) else lcm(a, b)
                val label = if (askEbob) "EBOB($a, $b)" else "EKOK($a, $b)"
                val isTrue = rng.nextBoolean()
                val wrongVal = if (askEbob) ans * 2 else ans / 2
                ComputedProblem(
                    topic = topic,
                    questionText = "$label değeri aşağıdakilerden hangisidir?",
                    correctAnswer = "$ans",
                    distractors = listOf("${ans + factor}", "${maxOf(2, wrongVal)}", "${ans + m1}").distinct().filter { it != "$ans" }.take(3),
                    explanation = "$a = $factor×$m1 ve $b = $factor×$m2 olduğundan EBOB = $factor, EKOK = ${lcm(a, b)} olur.",
                    conceptTerm = if (askEbob) "EBOB (En Büyük Ortak Bölen)" else "EKOK (En Küçük Ortak Kat)",
                    conceptDef = "İki pozitif tam sayının çarpımı, EBOB ve EKOK değerlerinin çarpımına eşittir: a·b = EBOB(a,b)·EKOK(a,b).",
                    blankBefore = "$a ve $b sayılarının ${if (askEbob) "en büyük ortak böleni" else "en küçük ortak katı"}",
                    blankAfter = "olarak hesaplanır.",
                    tfStatement = if (isTrue) "$label = $ans olarak bulunur." else "$label = $wrongVal olarak bulunur.",
                    tfIsTrue = isTrue,
                    wordBefore = "Aralarında asal iki pozitif tam sayının EBOB değeri her zaman",
                    wordCorrect = "1",
                    wordWrong = "sayıların çarpımı",
                    wordAfter = "olarak bulunur.",
                    openPrompt = "$a ve $b sayılarının $label değerini hangi adımlarla bulacağınızı yazınız.",
                    openHint = "Asal çarpan algoritmasını veya ortak bölenleri açıkla."
                )
            }

            lower.contains("karekök") || lower.contains("köklü") -> {
                val n1 = rng.nextInt(3, 13)
                val n2 = rng.nextInt(2, 10)
                val sq1 = n1 * n1
                val sq2 = n2 * n2
                val sum = n1 + n2
                val isTrue = rng.nextBoolean()
                ComputedProblem(
                    topic = topic,
                    questionText = "√$sq1 + √$sq2 işleminin sonucu kaçtır?",
                    correctAnswer = "$sum",
                    distractors = listOf("${abs(n1 - n2) + 1}", "${sum + 2}", "${n1 * n2}").distinct().filter { it != "$sum" }.take(3),
                    explanation = "√$sq1 = $n1 ve √$sq2 = $n2 olduğu için toplam $n1 + $n2 = $sum olur.",
                    conceptTerm = "Tam Kare ve Karekök",
                    conceptDef = "Pozitif bir sayının karesi olan sayılara tam kare denir; √(a²) = |a| eşitliği geçerlidir.",
                    blankBefore = "√$sq1 + √$sq2 işleminin sonucu",
                    blankAfter = "sayısına eşittir.",
                    tfStatement = if (isTrue) "√$sq1 sayısı rasyonel bir sayı olup $n1 değerine eşittir." else "√$sq1 sayısı irrasyonel bir sayıdır.",
                    tfIsTrue = isTrue,
                    wordBefore = "Karekök dışına tam çıkamayan √2, √3, √5 gibi sayılar",
                    wordCorrect = "irrasyonel",
                    wordWrong = "tam sayı",
                    wordAfter = "sayı kümesine aittir.",
                    openPrompt = "√$sq1 + √$sq2 işleminin neden √${sq1 + sq2} şeklinde toplanamayacağını açıklayınız.",
                    openHint = "Önce tam kare köklerin değerlerini dışarı çıkararak karşılaştır."
                )
            }

            lower.contains("olasılık") || lower.contains("permütasyon") || lower.contains("kombinasyon") -> {
                val red = rng.nextInt(2, 6)
                val blue = rng.nextInt(2, 6)
                val total = red + blue
                val g = gcd(red, total)
                val ans = "${red / g}/${total / g}"
                val isTrue = rng.nextBoolean()
                ComputedProblem(
                    topic = topic,
                    questionText = "Bir torbada $red kırmızı ve $blue mavi özdeş top vardır. Rastgele çekilen bir topun kırmızı olma olasılığı kaçtır?",
                    correctAnswer = ans,
                    distractors = listOf("$blue/$total", "1/$total", "$red/${blue + 1}").distinct().filter { it != ans }.take(3),
                    explanation = "Olasılık = İstenen durum / Tüm durumlar = $red / $total = $ans.",
                    conceptTerm = "Basit Olayların Olasılığı",
                    conceptDef = "Bir olayın olma olasılığı P(A) = s(A) / s(E) olup her zaman 0 ile 1 arasındadır.",
                    blankBefore = "İmkânsız olayın olasılığı 0 iken, kesin olayın olasılığı",
                    blankAfter = "değerine eşittir.",
                    tfStatement = if (isTrue) "Bir olayın olma olasılığı ile olmama olasılığının toplamı 1'dir." else "Bir olayın olasılık değeri 1'den büyük olabilir.",
                    tfIsTrue = isTrue,
                    wordBefore = "Bir olayın gerçekleşme ihtimalini bulmak için istenen durum sayısı, tüm olası durumların sayısına",
                    wordCorrect = "bölünür",
                    wordWrong = "çarpılır",
                    wordAfter = ".",
                    openPrompt = "$red kırmızı ve $blue mavi top bulunan torbadan mavi çekme olasılığının nasıl hesaplanacağını yazınız.",
                    openHint = "Örnek uzay ve istenen olay sayısını belirt."
                )
            }

            lower.contains("logaritma") || lower.contains("üstel") -> {
                val base = listOf(2, 3, 5).random(rng)
                val exp = rng.nextInt(2, 5)
                val arg = base.toDouble().pow(exp.toDouble()).toInt()
                val isTrue = rng.nextBoolean()
                ComputedProblem(
                    topic = topic,
                    questionText = "log_$base($arg) ifadesinin değeri kaçtır?",
                    correctAnswer = "$exp",
                    distractors = listOf("${exp + 1}", "${exp + 2}", "$base").distinct().filter { it != "$exp" }.take(3),
                    explanation = "$base^$exp = $arg olduğundan log_$base($arg) = $exp bulunur.",
                    conceptTerm = "Logaritma Fonksiyonu",
                    conceptDef = "Üstel fonksiyonun tersidir: a^x = b ⇔ log_a(b) = x (a > 0, a ≠ 1, b > 0).",
                    blankBefore = "$base^$exp = $arg olduğuna göre log_$base($arg) değeri",
                    blankAfter = "olarak bulunur.",
                    tfStatement = if (isTrue) "log_a(1) değeri taban ne olursa olsun (a>0, a≠1) her zaman 0'dır." else "log_a(0) ifadesi reel sayılarda tanımlıdır.",
                    tfIsTrue = isTrue,
                    wordBefore = "Aynı tabandaki iki logaritmanın toplamı, içlerindeki sayıların",
                    wordCorrect = "çarpımının",
                    wordWrong = "toplamının",
                    wordAfter = "logaritmasına eşittir.",
                    openPrompt = "Logaritma fonksiyonunun tanım kümesi şartlarını ($base tabanı üzerinden) açıklayınız.",
                    openHint = "Taban ve logaritması alınan sayının işaret şartlarını yaz."
                )
            }

            lower.contains("türev") -> {
                val a = rng.nextInt(2, 7)
                val b = rng.nextInt(2, 9)
                val x0 = rng.nextInt(1, 5)
                // f(x) = a*x^2 + b*x -> f'(x) = 2a*x + b
                val derivAtX0 = 2 * a * x0 + b
                val isTrue = rng.nextBoolean()
                ComputedProblem(
                    topic = topic,
                    questionText = "f(x) = ${a}x² + ${b}x fonksiyonu için f'($x0) türev değeri kaçtır?",
                    correctAnswer = "$derivAtX0",
                    distractors = listOf("${a * x0 + b}", "${derivAtX0 + a}", "${2 * a * x0}").distinct().filter { it != "$derivAtX0" }.take(3),
                    explanation = "Kuvvet kuralı ile f'(x) = ${2 * a}x + $b olur. x = $x0 yazılırsa ${2 * a}·$x0 + $b = $derivAtX0.",
                    conceptTerm = "Türevin Geometrik Yorumu",
                    conceptDef = "Bir f(x) fonksiyonunun x₀ noktasındaki türevi f'(x₀), grafiğe o noktadan çizilen teğetin eğimidir.",
                    blankBefore = "f(x) = ${a}x² + ${b}x fonksiyonunun x = $x0 noktasındaki türevi",
                    blankAfter = "değerine eşittir.",
                    tfStatement = if (isTrue) "Sabit bir fonksiyonun türevi her noktada sıfırdır." else "Bir fonksiyonun sürekli olduğu her noktada mutlaka türevi vardır.",
                    tfIsTrue = isTrue,
                    wordBefore = "Bir fonksiyonun x₀ noktasındaki birinci türevi, o noktadan çizilen",
                    wordCorrect = "teğetin eğimini",
                    wordWrong = "alanın hacmini",
                    wordAfter = "verir.",
                    openPrompt = "f(x) = ${a}x² + ${b}x fonksiyonunun türevini adım adım alıp x = $x0 noktasındaki anlamını açıklayınız.",
                    openHint = "Kuvvet kuralı ve teğet eğimi kavramından bahset."
                )
            }

            lower.contains("integral") -> {
                val a = rng.nextInt(1, 5)
                val upper = rng.nextInt(2, 5)
                // ∫_0^upper (2a*x) dx = a * upper^2
                val result = a * upper * upper
                val isTrue = rng.nextBoolean()
                ComputedProblem(
                    topic = topic,
                    questionText = "∫₀^$upper (${2 * a}x) dx belirli integralinin değeri kaçtır?",
                    correctAnswer = "$result",
                    distractors = listOf("${2 * a * upper}", "${result + a}", "${result * 2}").distinct().filter { it != "$result" }.take(3),
                    explanation = "${2 * a}x ifadesinin belirsiz integrali ${a}x² + C'dir. Sınırlar [0, $upper] yazılırsa $a·$upper² - 0 = $result.",
                    conceptTerm = "Belirli İntegral",
                    conceptDef = "Riemann toplamının limitidir ve pozitif fonksiyonlarda eğri ile x ekseni arasındaki alanı verir.",
                    blankBefore = "∫₀^$upper (${2 * a}x) dx belirli integralinin sonucu",
                    blankAfter = "olarak hesaplanır.",
                    tfStatement = if (isTrue) "İntegral alma işlemi türev alma işleminin tersidir." else "Belirsiz integralin sonucunda integral sabiti C yazılmaz.",
                    tfIsTrue = isTrue,
                    wordBefore = "Belirli integralde alt ve üst sınırlar yer değiştirirse integralin",
                    wordCorrect = "işareti değişir",
                    wordWrong = "değeri sıfırlanır",
                    wordAfter = ".",
                    openPrompt = "Belirli integral ile belirsiz integral arasındaki temel farkı açıklayınız.",
                    openHint = "Sınır değerleri, reel sayı sonucu ve +C sabitine değin."
                )
            }

            lower.contains("trigonometri") -> {
                val angles = listOf(
                    Triple("sin(30°) + cos(60°)", "1", "sin(30°)=1/2 ve cos(60°)=1/2 olduğundan toplam 1'dir."),
                    Triple("tan(45°) + cot(45°)", "2", "tan(45°)=1 ve cot(45°)=1 olduğundan toplam 2'dir."),
                    Triple("sin²(25°) + cos²(25°)", "1", "Temel trigonometrik özdeşlik gereği sin²x + cos²x = 1'dir."),
                    Triple("2·sin(30°) + 3·tan(45°)", "4", "2·(1/2) + 3·1 = 1 + 3 = 4 bulunur.")
                )
                val pick = angles.random(rng)
                val isTrue = rng.nextBoolean()
                ComputedProblem(
                    topic = topic,
                    questionText = "${pick.first} ifadesinin değeri kaçtır?",
                    correctAnswer = pick.second,
                    distractors = listOf("0", "1/2", "3", "5").filter { it != pick.second }.take(3),
                    explanation = pick.third,
                    conceptTerm = "Temel Trigonometrik Özdeşlik",
                    conceptDef = "Birim çember üzerindeki her (cos α, sin α) noktası için sin²α + cos²α = 1 eşitliği sağlanır.",
                    blankBefore = "${pick.first} ifadesinin sadeleşmiş sayısal değeri",
                    blankAfter = "olarak bulunur.",
                    tfStatement = if (isTrue) "Birim çemberde sin²α + cos²α = 1 özdeşliği her α açısı için geçerlidir." else "cos(α) değeri reel sayılarda 2 olabilir.",
                    tfIsTrue = isTrue,
                    wordBefore = "Bir dik üçgende karşı dik kenarın hipotenüse oranı",
                    wordCorrect = "sinüs",
                    wordWrong = "kosinüs",
                    wordAfter = "olarak tanımlanır.",
                    openPrompt = "Birim çember üzerinde sinüs ve kosinüs değerlerinin hangi eksenlerle ilişkili olduğunu açıklayınız.",
                    openHint = "x ekseni (kosinüs) ve y ekseni (sinüs) ilişkisini anlat."
                )
            }

            lower.contains("denklem") || lower.contains("cebirsel") || lower.contains("fonksiyon") || lower.contains("polinom") -> {
                val m = rng.nextInt(2, 7)
                val xAns = rng.nextInt(2, 11)
                val c = rng.nextInt(3, 18)
                val rhs = m * xAns + c
                val isTrue = rng.nextBoolean()
                ComputedProblem(
                    topic = topic,
                    questionText = "${m}x + $c = $rhs denklemini sağlayan x değeri kaçtır?",
                    correctAnswer = "$xAns",
                    distractors = listOf("${xAns + 1}", "${xAns + 2}", "${maxOf(1, xAns - 1)}").distinct().take(3),
                    explanation = "${m}x = $rhs - $c = ${m * xAns} ⇒ x = ${m * xAns} / $m = $xAns.",
                    conceptTerm = "Doğrusal Denklem Çözümü",
                    conceptDef = "Denklemin her iki tarafına aynı işlem uygulanarak bilinmeyen yalnız bırakılır.",
                    blankBefore = "${m}x + $c = $rhs eşitliğini sağlayan x değeri",
                    blankAfter = "sayısıdır.",
                    tfStatement = if (isTrue) "${m}x + $c = $rhs denkleminin kökü x = $xAns olur." else "${m}x + $c = $rhs denkleminin kökü x = ${xAns + 2} olur.",
                    tfIsTrue = isTrue,
                    wordBefore = "Bir eşitliğin bir tarafından diğer tarafına geçen terimin",
                    wordCorrect = "işareti değişir",
                    wordWrong = "değeri ikiye katlanır",
                    wordAfter = ".",
                    openPrompt = "${m}x + $c = $rhs denkleminin çözüm adımlarını sırayla açıklayınız.",
                    openHint = "Sabit terimi karşıya atıp katsayıya bölme adımlarını yaz."
                )
            }

            lower.contains("yüzde") || lower.contains("oran") -> {
                val baseVal = listOf(120, 150, 200, 250, 300, 400).random(rng)
                val pct = listOf(10, 20, 25, 30, 40).random(rng)
                val ans = (baseVal * pct) / 100
                val isTrue = rng.nextBoolean()
                ComputedProblem(
                    topic = topic,
                    questionText = "$baseVal TL etiket fiyatlı bir ürüne %$pct indirim yapılırsa indirim tutarı kaç TL olur?",
                    correctAnswer = "$ans",
                    distractors = listOf("${ans + 10}", "${baseVal - ans}", "${ans / 2 + 5}").distinct().filter { it != "$ans" }.take(3),
                    explanation = "$baseVal × ($pct / 100) = $ans TL indirim uygulanır.",
                    conceptTerm = "Yüzde Hesaplama",
                    conceptDef = "Bir A sayısının %k'sı, A · (k / 100) işlemi ile bulunur.",
                    blankBefore = "$baseVal sayısının %$pct değeri",
                    blankAfter = "olarak hesaplanır.",
                    tfStatement = if (isTrue) "$baseVal sayısının %$pct kadarı $ans eder." else "$baseVal sayısının %$pct kadarı ${ans + 15} eder.",
                    tfIsTrue = isTrue,
                    wordBefore = "İki çokluktan biri artarken diğeri de aynı oranda artıyorsa aralarında",
                    wordCorrect = "doğru orantı",
                    wordWrong = "ters orantı",
                    wordAfter = "vardır.",
                    openPrompt = "Bir ürüne önce %$pct indirim yapıldığında yeni satış fiyatının nasıl hesaplanacağını açıklayınız.",
                    openHint = "İndirim tutarını bulup başlangıç fiyatından çıkarmayı anlat."
                )
            }

            lower.contains("kesir") || lower.contains("rasyonel") || lower.contains("ondalık") -> {
                val denom = listOf(4, 5, 6, 8, 9, 10, 12).random(rng)
                val n1 = rng.nextInt(1, denom)
                val n2 = rng.nextInt(1, denom)
                val sumNum = n1 + n2
                val g = gcd(sumNum, denom)
                val ans = if (denom / g == 1) "${sumNum / g}" else "${sumNum / g}/${denom / g}"
                val isTrue = rng.nextBoolean()
                ComputedProblem(
                    topic = topic,
                    questionText = "$n1/$denom + $n2/$denom işleminin en sade sonucu kaçtır?",
                    correctAnswer = ans,
                    distractors = listOf("$sumNum/${denom * 2}", "${n1 + n2 + 1}/$denom", "${maxOf(1, abs(n1 - n2))}/$denom")
                        .distinct().filter { it != ans }.take(3),
                    explanation = "Paydaları eşit kesirler toplanırken paylar toplanır: ($n1 + $n2)/$denom = $sumNum/$denom = $ans.",
                    conceptTerm = "Kesirlerde ve Rasyonel Sayılarda Toplama",
                    conceptDef = "Paydaları eşit olan kesirler toplanırken ortak payda korunur, paylar toplanır ve sonuç sadeleştirilir.",
                    blankBefore = "$n1/$denom + $n2/$denom işleminin en sade hali",
                    blankAfter = "kesrine eşittir.",
                    tfStatement = if (isTrue) "$n1/$denom + $n2/$denom = $ans eşitliği doğrudur." else "$n1/$denom + $n2/$denom = $sumNum/${denom * 2} eşitliği doğrudur.",
                    tfIsTrue = isTrue,
                    wordBefore = "Paydaları eşit iki kesir toplanırken paylar toplanır, ortak payda ise",
                    wordCorrect = "aynen yazılır",
                    wordWrong = "toplanır",
                    wordAfter = ".",
                    openPrompt = "$n1/$denom + $n2/$denom işleminin sonucunu sadeleştirme adımlarıyla açıklayınız.",
                    openHint = "Payları topla ve EBOB ile sadeleştir."
                )
            }

            lower.contains("tam sayı") -> {
                val a = rng.nextInt(4, 18)
                val b = rng.nextInt(2, 9)
                val c = rng.nextInt(3, 12)
                val ans = (-a) + b * c
                val isTrue = rng.nextBoolean()
                ComputedProblem(
                    topic = topic,
                    questionText = "(-$a) + ($b × $c) tam sayı işleminin sonucu kaçtır?",
                    correctAnswer = "$ans",
                    distractors = listOf("${a + b * c}", "${(-a + b) * c}", "${ans - 2 * b}")
                        .distinct().filter { it != "$ans" }.take(3),
                    explanation = "Önce çarpma yapılır: $b × $c = ${b * c}. Sonra (-$a) + ${b * c} = $ans bulunur.",
                    conceptTerm = "Tam Sayılarda İşlemler",
                    conceptDef = "Zıt işaretli iki tam sayı toplanırken mutlak değerce büyükten küçük çıkarılır ve büyüğün işareti yazılır.",
                    blankBefore = "(-$a) + ($b × $c) işleminin sonucu",
                    blankAfter = "tam sayısına eşittir.",
                    tfStatement = if (isTrue) "İki negatif tam sayının çarpımı pozitif bir tam sayıdır." else "Bir negatif ile bir pozitif tam sayının çarpımı pozitiftir.",
                    tfIsTrue = isTrue,
                    wordBefore = "Aynı işaretli iki tam sayının çarpımı her zaman",
                    wordCorrect = "pozitif",
                    wordWrong = "negatif",
                    wordAfter = "bir tam sayı verir.",
                    openPrompt = "(-$a) + ($b × $c) işleminde işaret kuralları ve işlem önceliğini nasıl uyguladığınızı açıklayınız.",
                    openHint = "Önce çarpma, sonra zıt işaretli toplama kuralını anlat."
                )
            }

            lower.contains("üçgen") || lower.contains("açı") || lower.contains("çember") || lower.contains("geometri") || lower.contains("dörtgen") -> {
                val variant = rng.nextInt(0, 3)
                when (variant) {
                    0 -> {
                        val a1 = rng.nextInt(32, 78)
                        val a2 = rng.nextInt(35, 82)
                        val a3 = 180 - a1 - a2
                        val isTrue = rng.nextBoolean()
                        ComputedProblem(
                            topic = topic,
                            questionText = "İç açılarından ikisi $a1° ve $a2° olan bir üçgenin üçüncü iç açısı kaç derecedir?",
                            correctAnswer = "$a3°",
                            distractors = listOf("${a3 + 10}°", "${abs(a1 - a2) + 20}°", "${180 - a1}°")
                                .distinct().filter { it != "$a3°" }.take(3),
                            explanation = "Bir üçgenin iç açıları toplamı 180° olduğundan 180° - ($a1° + $a2°) = $a3° bulunur.",
                            conceptTerm = "Üçgende İç Açılar Toplamı",
                            conceptDef = "Öklid düzlemindeki her üçgenin üç iç açısının ölçüleri toplamı daima 180°'dir.",
                            blankBefore = "İki iç açısı $a1° ve $a2° olan üçgenin üçüncü açısı",
                            blankAfter = "derecedir.",
                            tfStatement = if (isTrue) "Bir üçgenin iç açıları toplamı 180°, dış açıları toplamı 360°'dir." else "Bir üçgende iki tane geniş açı aynı anda bulunabilir.",
                            tfIsTrue = isTrue,
                            wordBefore = "Bir üçgenin iç açıları ölçüleri toplamı her zaman",
                            wordCorrect = "180°",
                            wordWrong = "360°",
                            wordAfter = "olarak sabittir.",
                            openPrompt = "İki açısı $a1° ve $a2° olan üçgenin üçüncü açısını ve üçgen çeşidini açıklayınız.",
                            openHint = "180° toplamından çıkar ve dar/dik/geniş açılı olma durumunu yorumla."
                        )
                    }
                    1 -> {
                        val r = rng.nextInt(3, 12)
                        val circumference = 2 * 3 * r
                        val isTrue = rng.nextBoolean()
                        ComputedProblem(
                            topic = topic,
                            questionText = "Yarıçapı $r cm olan bir çemberin çevre uzunluğu kaç cm'dir? (π = 3 alınız)",
                            correctAnswer = "$circumference",
                            distractors = listOf("${3 * r * r}", "${3 * r}", "${circumference + 6}")
                                .distinct().filter { it != "$circumference" }.take(3),
                            explanation = "Çemberin çevresi Ç = 2·π·r = 2 × 3 × $r = $circumference cm.",
                            conceptTerm = "Çemberin Çevresi",
                            conceptDef = "Yarıçapı r olan bir çemberin çevresi 2πr, dairenin alanı ise πr² formülüyle bulunur.",
                            blankBefore = "Yarıçapı $r cm olan çemberin çevresi (π = 3)",
                            blankAfter = "cm'dir.",
                            tfStatement = if (isTrue) "Çemberin çapı, yarıçapının 2 katına eşittir." else "Dairenin alanı 2πr formülü ile hesaplanır.",
                            tfIsTrue = isTrue,
                            wordBefore = "Bir çemberde merkezden geçen en uzun kirişe",
                            wordCorrect = "çap",
                            wordWrong = "teğet",
                            wordAfter = "denir.",
                            openPrompt = "Yarıçapı $r cm olan dairenin çevresi ile alanının nasıl farklı hesaplandığını açıklayınız.",
                            openHint = "2πr ve πr² formüllerini karşılaştır."
                        )
                    }
                    else -> {
                        val k = rng.nextInt(2, 6)
                        val leg1 = 3 * k
                        val leg2 = 4 * k
                        val hyp = 5 * k
                        val isTrue = rng.nextBoolean()
                        ComputedProblem(
                            topic = topic,
                            questionText = "Dik kenar uzunlukları $leg1 cm ve $leg2 cm olan bir dik üçgenin hipotenüs uzunluğu kaç cm'dir?",
                            correctAnswer = "$hyp",
                            distractors = listOf("${leg1 + leg2}", "${hyp + k}", "${leg2 + 2}")
                                .distinct().filter { it != "$hyp" }.take(3),
                            explanation = "Pisagor Teoremi'ne göre c² = $leg1² + $leg2² = ${leg1 * leg1 + leg2 * leg2} ⇒ c = $hyp cm (3-4-5 özel üçgeninin $k katı).",
                            conceptTerm = "Pisagor Bağıntısı",
                            conceptDef = "Bir dik üçgende dik kenarların kareleri toplamı, hipotenüsün karesine eşittir: a² + b² = c².",
                            blankBefore = "Dik kenarları $leg1 cm ve $leg2 cm olan dik üçgende hipotenüs",
                            blankAfter = "cm'dir.",
                            tfStatement = if (isTrue) "Dik üçgende en uzun kenar her zaman 90°'lik açının karşısındaki hipotenüstür." else "Pisagor bağıntısı yalnızca eşkenar üçgenlerde geçerlidir.",
                            tfIsTrue = isTrue,
                            wordBefore = "Bir dik üçgende 90°'lik açının karşısında bulunan en uzun kenara",
                            wordCorrect = "hipotenüs",
                            wordWrong = "kenarortay",
                            wordAfter = "denir.",
                            openPrompt = "Dik kenarları $leg1 cm ve $leg2 cm olan üçgende Pisagor bağıntısını adım adım uygulayınız.",
                            openHint = "a² + b² = c² formülünü yaz."
                        )
                    }
                }
            }

            else -> {
                // Multi-template grade-appropriate randomized arithmetic, algebra, and geometry problems
                val template = rng.nextInt(0, 3)
                if (grade <= 6) {
                    when (template) {
                        0 -> {
                            val a = rng.nextInt(14, 52)
                            val b = rng.nextInt(3, 11)
                            val c = rng.nextInt(8, 29)
                            val ans = a + b * c
                            val isTrue = rng.nextBoolean()
                            ComputedProblem(
                                topic = topic,
                                questionText = "İşlem önceliğine göre $a + $b × $c işleminin sonucu kaçtır?",
                                correctAnswer = "$ans",
                                distractors = listOf("${(a + b) * c}", "${ans + b}", "${ans - 4}").distinct().filter { it != "$ans" }.take(3),
                                explanation = "İşlem önceliği gereği önce çarpma yapılır: $b × $c = ${b * c}, sonra $a + ${b * c} = $ans.",
                                conceptTerm = "İşlem Önceliği",
                                conceptDef = "Sırasıyla parantez içi, üslü ifadeler, çarpma/bölme ve son olarak toplama/çıkarma yapılır.",
                                blankBefore = "İşlem önceliğine göre $a + $b × $c işleminin sonucu",
                                blankAfter = "olarak bulunur.",
                                tfStatement = if (isTrue) "Çarpma işlemi, toplama işlemine göre işlem önceliğine sahiptir." else "İşlem önceliğinde toplama her zaman çarpmadan önce yapılır.",
                                tfIsTrue = isTrue,
                                wordBefore = "Parantez bulunmayan bir ifadede çarpma ve bölme işlemleri, toplama ve çıkarmadan",
                                wordCorrect = "önce",
                                wordWrong = "sonra",
                                wordAfter = "yapılır.",
                                openPrompt = "$a + $b × $c işleminde neden önce $b × $c işleminin yapılması gerektiğini açıklayınız.",
                                openHint = "Matematikte işlem önceliği sırasından bahset."
                            )
                        }
                        1 -> {
                            val a = rng.nextInt(6, 20)
                            val b = rng.nextInt(4, 15)
                            val c = rng.nextInt(3, 9)
                            val ans = (a + b) * c
                            val isTrue = rng.nextBoolean()
                            ComputedProblem(
                                topic = topic,
                                questionText = "($a + $b) × $c işleminin sonucu kaçtır?",
                                correctAnswer = "$ans",
                                distractors = listOf("${a + b * c}", "${ans + c}", "${ans - a}").distinct().filter { it != "$ans" }.take(3),
                                explanation = "Önce parantez içi toplanır: $a + $b = ${a + b}. Sonra ${a + b} × $c = $ans.",
                                conceptTerm = "Parantezli İşlemler ve Dağılma Özelliği",
                                conceptDef = "Parantez içindeki işlemler her zaman ilk sırada yapılır; çarpmanın toplama üzerine dağılma özelliği uygulanabilir.",
                                blankBefore = "($a + $b) × $c işleminin sayısal değeri",
                                blankAfter = "olarak hesaplanır.",
                                tfStatement = if (isTrue) "($a + $b) × $c ifadesi ($a × $c) + ($b × $c) toplamına eşittir." else "($a + $b) × $c işleminin sonucu $a + ($b × $c) ile aynıdır.",
                                tfIsTrue = isTrue,
                                wordBefore = "Çok adımlı bir matematiksel ifadede ilk olarak",
                                wordCorrect = "parantez içi",
                                wordWrong = "çıkarma işlemi",
                                wordAfter = "hesaplanır.",
                                openPrompt = "($a + $b) × $c işlemini çarpmanın toplama üzerine dağılma özelliğiyle açıklayınız.",
                                openHint = "Her terimi dışarıdaki çarpanla çarpıp topla."
                            )
                        }
                        else -> {
                            val divisor = rng.nextInt(4, 12)
                            val quotient = rng.nextInt(6, 18)
                            val dividend = divisor * quotient
                            val extra = rng.nextInt(5, 25)
                            val ans = quotient + extra
                            val isTrue = rng.nextBoolean()
                            ComputedProblem(
                                topic = topic,
                                questionText = "$dividend ÷ $divisor + $extra işleminin sonucu kaçtır?",
                                correctAnswer = "$ans",
                                distractors = listOf("${ans + divisor}", "${quotient * 2}", "${maxOf(1, ans - 3)}").distinct().filter { it != "$ans" }.take(3),
                                explanation = "Önce bölme yapılır: $dividend ÷ $divisor = $quotient. Ardından $quotient + $extra = $ans.",
                                conceptTerm = "Bölme ve Toplama İşlem Sırası",
                                conceptDef = "Bölme işlemi toplama işleminden önceliklidir; önce bölüm hesaplanır.",
                                blankBefore = "$dividend ÷ $divisor + $extra işleminin sonucu",
                                blankAfter = "sayısına eşittir.",
                                tfStatement = if (isTrue) "$dividend ÷ $divisor = $quotient olduğundan toplam $ans olur." else "$dividend ÷ $divisor + $extra işleminin sonucu ${ans + 5} olur.",
                                tfIsTrue = isTrue,
                                wordBefore = "Kalınsız bir bölme işleminde bölünen sayı, bölen ile bölümün",
                                wordCorrect = "çarpımına",
                                wordWrong = "farkına",
                                wordAfter = "eşittir.",
                                openPrompt = "$dividend ÷ $divisor + $extra işleminde izlediğiniz adımları sırayla yazınız.",
                                openHint = "Önce bölmeyi yapıp sonra toplamayı açıkla."
                            )
                        }
                    }
                } else {
                    when (template) {
                        0 -> {
                            val a = rng.nextInt(4, 16)
                            val b = rng.nextInt(3, 13)
                            val perimeter = 2 * (a + b)
                            val area = a * b
                            val isTrue = rng.nextBoolean()
                            ComputedProblem(
                                topic = topic,
                                questionText = "Kenar uzunlukları $a cm ve $b cm olan bir dikdörtgenin alanı kaç cm²'dir?",
                                correctAnswer = "$area",
                                distractors = listOf("$perimeter", "${area + a}", "${a + b}").distinct().filter { it != "$area" }.take(3),
                                explanation = "Dikdörtgenin alanı iki komşu kenarının çarpımıdır: $a × $b = $area cm².",
                                conceptTerm = "Dikdörtgende Alan ve Çevre",
                                conceptDef = "Kenarları a ve b olan dikdörtgenin çevresi 2(a+b), alanı ise a·b formülü ile hesaplanır.",
                                blankBefore = "Kenarları $a cm ve $b cm olan dikdörtgenin alanı",
                                blankAfter = "cm²'dir.",
                                tfStatement = if (isTrue) "Kenarları $a cm ve $b cm olan dikdörtgenin alanı $area cm²'dir." else "Kenarları $a cm ve $b cm olan dikdörtgenin alanı $perimeter cm²'dir.",
                                tfIsTrue = isTrue,
                                wordBefore = "Bir dikdörtgenin çevresi hesaplanırken kısa ve uzun kenar toplanıp",
                                wordCorrect = "2 ile çarpılır",
                                wordWrong = "birbirine bölünür",
                                wordAfter = ".",
                                openPrompt = "Alan ve çevre kavramları arasındaki farkı bir dikdörtgen örneğiyle açıklayınız.",
                                openHint = "İç bölge kaplama (alan) ve sınır uzunluğu (çevre) farkını yaz."
                            )
                        }
                        1 -> {
                            val a = rng.nextInt(2, 8)
                            val b = rng.nextInt(3, 15)
                            val k = rng.nextInt(2, 9)
                            val ans = a * k - b
                            val isTrue = rng.nextBoolean()
                            ComputedProblem(
                                topic = topic,
                                questionText = "f(x) = ${a}x - $b fonksiyonu için f($k) değeri kaçtır?",
                                correctAnswer = "$ans",
                                distractors = listOf("${a * k + b}", "${ans + a}", "${a + k - b}").distinct().filter { it != "$ans" }.take(3),
                                explanation = "x yerine $k yazılır: f($k) = $a·$k - $b = ${a * k} - $b = $ans.",
                                conceptTerm = "Doğrusal Fonksiyon Değeri",
                                conceptDef = "f(x) = ax + b kuralında tanım kümesindeki x değeri yerine yazılarak görüntü bulunur.",
                                blankBefore = "f(x) = ${a}x - $b için f($k) değeri",
                                blankAfter = "olarak bulunur.",
                                tfStatement = if (isTrue) "f(x) = ${a}x - $b fonksiyonunda f($k) = $ans olur." else "f(x) = ${a}x - $b fonksiyonunda f($k) = ${a * k + b} olur.",
                                tfIsTrue = isTrue,
                                wordBefore = "f(x) = ${a}x - $b doğrusal fonksiyonunun grafiğinin eğimi",
                                wordCorrect = "$a",
                                wordWrong = "-$b",
                                wordAfter = "sayısına eşittir.",
                                openPrompt = "f(x) = ${a}x - $b fonksiyonunda x = $k noktasının görüntüsünü nasıl bulduğunuzu anlatınız.",
                                openHint = "Değişken yerine sayı koyma ve işlem sırasını belirt."
                            )
                        }
                        else -> {
                            val n1 = rng.nextInt(12, 34)
                            val n2 = rng.nextInt(16, 42)
                            val n3 = 3 * rng.nextInt(15, 36) - n1 - n2
                            val avg = (n1 + n2 + n3) / 3
                            val isTrue = rng.nextBoolean()
                            ComputedProblem(
                                topic = topic,
                                questionText = "$n1, $n2 ve $n3 sayılarının aritmetik ortalaması kaçtır?",
                                correctAnswer = "$avg",
                                distractors = listOf("${avg + 3}", "${maxOf(1, avg - 2)}", "${n1 + n2 + n3}").distinct().filter { it != "$avg" }.take(3),
                                explanation = "Aritmetik ortalama = ($n1 + $n2 + $n3) / 3 = ${n1 + n2 + n3} / 3 = $avg.",
                                conceptTerm = "Aritmetik Ortalama ve Veri Analizi",
                                conceptDef = "Bir veri grubundaki tüm sayıların toplamının veri adedine bölünmesiyle aritmetik ortalama bulunur.",
                                blankBefore = "$n1, $n2 ve $n3 verilerinin aritmetik ortalaması",
                                blankAfter = "değerine eşittir.",
                                tfStatement = if (isTrue) "$n1, $n2 ve $n3 sayılarının aritmetik ortalaması $avg olur." else "Aritmetik ortalama bulunurken verilerin toplamı en büyük sayıya bölünür.",
                                tfIsTrue = isTrue,
                                wordBefore = "Bir veri grubunun aritmetik ortalamasını bulmak için verilerin toplamı",
                                wordCorrect = "veri sayısına bölünür",
                                wordWrong = "veri sayısıyla çarpılır",
                                wordAfter = ".",
                                openPrompt = "$n1, $n2 ve $n3 sayılarının aritmetik ortalamasının nasıl hesaplandığını açıklayınız.",
                                openHint = "Verileri topla ve 3'e böl."
                            )
                        }
                    }
                }
            }
        }
    }

    private fun generatePhysicsProblem(grade: Int, topic: String, rng: Random): ComputedProblem {
        val lower = topic.lowercase()
        return when {
            lower.contains("elektrik") || lower.contains("ohm") || lower.contains("devre") -> {
                val i = rng.nextInt(2, 8)
                val r = rng.nextInt(3, 12)
                val v = i * r
                val isTrue = rng.nextBoolean()
                ComputedProblem(
                    topic = topic,
                    questionText = "Direnci $r Ω olan bir iletkenden $i A elektrik akımı geçtiğine göre uçları arasındaki potansiyel farkı (V) kaç Volt'tur?",
                    correctAnswer = "$v V",
                    distractors = listOf("${i + r} V", "${v + r} V", "${r * 2} V").distinct().filter { it != "$v V" }.take(3),
                    explanation = "Ohm Yasası'na göre V = I · R = $i · $r = $v Volt bulunur.",
                    conceptTerm = "Ohm Yasası (V = I · R)",
                    conceptDef = "Sabit sıcaklıkta bir iletkenin uçları arasındaki potansiyel farkının akım şiddetine oranı sabittir ve direnci verir.",
                    blankBefore = "Ohm Yasası'na göre potansiyel farkı, akım şiddeti ile",
                    blankAfter = "değerinin çarpımına eşittir.",
                    tfStatement = if (isTrue) "Seri bağlı dirençlerde tüm dirençlerden geçen akım şiddeti eşittir." else "Paralel bağlı kollarda her zaman eşdeğer direnç en büyük dirençten büyük olur.",
                    tfIsTrue = isTrue,
                    wordBefore = "Bir elektrik devresinde akım şiddetini ölçmek için devreye seri bağlanan ölçüm aracına",
                    wordCorrect = "ampermetre",
                    wordWrong = "voltmetre",
                    wordAfter = "denir.",
                    openPrompt = "Ohm Yasası (V = I·R) bağıntısının günlük yaşamda kullandığımız devrelerde ne anlama geldiğini açıklayınız.",
                    openHint = "Gerilim, akım ve direnç arasındaki doğru/ters orantıdan bahset."
                ).copy(correctAnswer = "$v V")
            }

            lower.contains("enerji") || lower.contains("iş") || lower.contains("güç") -> {
                val m = rng.nextInt(2, 9)
                val v = listOf(2, 4, 6, 8).random(rng)
                val ke = (m * v * v) / 2
                val isTrue = rng.nextBoolean()
                ComputedProblem(
                    topic = topic,
                    questionText = "Kütlesi $m kg olan bir cisim $v m/s süratle hareket etmektedir. Cismin kinetik enerjisi kaç Joule'dür?",
                    correctAnswer = "$ke J",
                    distractors = listOf("${m * v} J", "${ke * 2} J", "${ke + m} J").distinct().filter { it != "$ke J" }.take(3),
                    explanation = "Kinetik enerji Ek = 1/2 · m · v² = 1/2 · $m · $v² = $ke Joule.",
                    conceptTerm = "Kinetik Enerji",
                    conceptDef = "Cisimlerin hareketlerinden dolayı sahip oldukları enerjidir: Ek = ½mv².",
                    blankBefore = "Kütlesi $m kg, hızı $v m/s olan cismin kinetik enerjisi",
                    blankAfter = "Joule'dür.",
                    tfStatement = if (isTrue) "Sürtünmesiz bir sistemde mekanik enerji (kinetik + potansiyel) korunur." else "İşin SI birim sistemindeki birimi Newton'dur.",
                    tfIsTrue = isTrue,
                    wordBefore = "Birim zamanda yapılan işe fizikte",
                    wordCorrect = "güç",
                    wordWrong = "ivme",
                    wordAfter = "denir ve birimi Watt'tır.",
                    openPrompt = "Kinetik enerji ve yer çekimi potansiyel enerjisi arasındaki dönüşüme günlük hayattan bir örnek verip açıklayınız.",
                    openHint = "Yüksekten bırakılan top veya salıncak örneğini kullanabilirsin."
                )
            }

            else -> {
                val m = rng.nextInt(2, 10)
                val a = rng.nextInt(2, 8)
                val f = m * a
                val isTrue = rng.nextBoolean()
                ComputedProblem(
                    topic = topic,
                    questionText = "Sürtünmesiz yatay düzlemde duran $m kg kütleli cisme uygulanan net kuvvet cisme $a m/s² ivme kazandırıyor. Net kuvvet kaç Newton'dur?",
                    correctAnswer = "$f N",
                    distractors = listOf("${m + a} N", "${f + m} N", "${maxOf(1, f - a)} N").distinct().filter { it != "$f N" }.take(3),
                    explanation = "Newton'un II. Yasası (Dinamiğin Temel Prensibi): F_net = m · a = $m · $a = $f N.",
                    conceptTerm = "Newton'un Hareket Yasaları",
                    conceptDef = "F_net = m · a bağıntısına göre bir cismin ivmesi üzerine etki eden net kuvvetle doğru, kütlesiyle ters orantılıdır.",
                    blankBefore = "Newton'un ikinci yasasına göre net kuvvet, kütle ile",
                    blankAfter = "çarpımına eşittir.",
                    tfStatement = if (isTrue) "Bir cisme etki eden net kuvvet sıfır ise cisim durmaya veya sabit hızla hareket etmeye devam eder." else "Kuvvet skaler bir büyüklüktür.",
                    tfIsTrue = isTrue,
                    wordBefore = "Yönü ve doğrultusu olan kuvvet, hız ve ivme gibi büyüklükler",
                    wordCorrect = "vektörel",
                    wordWrong = "skaler",
                    wordAfter = "büyüklükler sınıfına girer.",
                    openPrompt = "Newton'un Eylemsizlik Yasası'nı araç içi emniyet kemeri örneğiyle açıklayınız.",
                    openHint = "Ani fren sırasında cismin hareket durumunu koruma isteğinden bahset."
                )
            }
        }
    }

    private fun generateChemistryProblem(grade: Int, topic: String, rng: Random): ComputedProblem {
        val moles = listOf(2, 3, 4, 5).random(rng)
        val elements = listOf(
            Triple("Karbon (C = 12 g/mol)", 12, "C"),
            Triple("Oksijen (O₂ = 32 g/mol)", 32, "O₂"),
            Triple("Su (H₂O = 18 g/mol)", 18, "H₂O"),
            Triple("Metan (CH₄ = 16 g/mol)", 16, "CH₄")
        )
        val el = elements.random(rng)
        val mass = moles * el.second
        val isTrue = rng.nextBoolean()
        return ComputedProblem(
            topic = topic,
            questionText = "$moles mol ${el.first} maddesi kaç gramdır?",
            correctAnswer = "$mass g",
            distractors = listOf("${el.second + moles} g", "${mass + el.second} g", "${mass / 2} g").distinct().filter { it != "$mass g" }.take(3),
            explanation = "n = m / M_A formülünden m = n · M_A = $moles × ${el.second} = $mass gram bulunur.",
            conceptTerm = "Mol Kavramı ve Avogadro Sayısı",
            conceptDef = "1 mol madde, Avogadro sayısı (6,02 × 10²³) kadar tanecik içerir ve kütlesi mol kütlesine (M_A) eşittir.",
            blankBefore = "1 mol maddenin içerdiği 6,02×10²³ tanecik sayısına",
            blankAfter = "sayısı denir.",
            tfStatement = if (isTrue) "Oda koşullarında pH değeri 7'den küçük olan sulu çözeltiler asidik özellik gösterir." else "Kimyasal tepkimelerde toplam kütle zamanla azalır.",
            tfIsTrue = isTrue,
            wordBefore = "Atom numarası aynı, kütle numarası (nötron sayısı) farklı olan atomlara",
            wordCorrect = "izotop",
            wordWrong = "izobar",
            wordAfter = "atomlar denir.",
            openPrompt = "İyonik bağ ile kovalent bağ arasındaki elektron alışverişi/ortaklaşması farkını açıklayınız.",
            openHint = "Metal-ametal ve ametal-ametal etkileşimlerini karşılaştır."
        )
    }

    private fun generateMindGameProblem(topic: String, rng: Random): ComputedProblem {
        val start = rng.nextInt(3, 12)
        val step = rng.nextInt(3, 8)
        val seq = List(4) { i -> start + i * step }
        val nextVal = start + 4 * step
        val isTrue = rng.nextBoolean()
        return ComputedProblem(
            topic = topic,
            questionText = "${seq.joinToString(", ")}, ? sayı örüntüsünde soru işareti yerine hangi sayı gelmelidir?",
            correctAnswer = "$nextVal",
            distractors = listOf("${nextVal + 1}", "${nextVal + step}", "${nextVal - 2}").distinct().filter { it != "$nextVal" }.take(3),
            explanation = "Örüntü her adımda +$step artmaktadır. ${seq.last()} + $step = $nextVal.",
            conceptTerm = "Aritmetik Örüntü ve Strateji",
            conceptDef = "Ardışık terimler arasındaki farkın sabit olduğu dizilerde kuralı bulmak hızlı çıkarım sağlar.",
            blankBefore = "${seq.joinToString(", ")} örüntüsünün 5. terimi",
            blankAfter = "sayısıdır.",
            tfStatement = if (isTrue) "Sudoku oyununda her satır, sütun ve bölgede rakamlar yalnızca bir kez kullanılır." else "Mangala oyununda amaç kendi hazinende en az taşı toplamaktır.",
            tfIsTrue = isTrue,
            wordBefore = "Strateji oyunlarında rakibin olası sonraki hamlesini önceden hesaplamaya",
            wordCorrect = "öngörü",
            wordWrong = "tesadüf",
            wordAfter = "denir.",
            openPrompt = "Bir Sudoku veya mantık bulmacasını çözerken kullandığınız sistematik eleme yöntemini açıklayınız.",
            openHint = "Kesin bilinen hücrelerden başlayarak olasılık azaltmayı anlat."
        )
    }

    private fun generateEconomyProblem(topic: String, rng: Random): ComputedProblem {
        val cost = listOf(100, 200, 400, 500).random(rng)
        val margin = listOf(15, 20, 25, 30).random(rng)
        val price = cost + (cost * margin / 100)
        val isTrue = rng.nextBoolean()
        return ComputedProblem(
            topic = topic,
            questionText = "Birim maliyeti $cost TL olan bir ürün %$margin kâr marjı ile satılırsa satış fiyatı kaç TL olur?",
            correctAnswer = "$price TL",
            distractors = listOf("${cost + margin} TL", "${price + 20} TL", "${cost - (cost * margin / 100)} TL").distinct().filter { it != "$price TL" }.take(3),
            explanation = "Kâr tutarı = $cost × $margin/100 = ${cost * margin / 100} TL. Satış fiyatı = $cost + ${cost * margin / 100} = $price TL.",
            conceptTerm = "Fırsat Maliyeti ve Piyasa Dengesi",
            conceptDef = "Bir tercihi seçerken vazgeçilen en iyi ikinci alternatifin değerine fırsat maliyeti denir.",
            blankBefore = "Genel fiyat düzeyinin sürekli ve belirgin şekilde yükselmesine",
            blankAfter = "denir.",
            tfStatement = if (isTrue) "Talep sabitken bir malın arzı artarsa denge fiyatının düşmesi beklenir." else "Kıtlık yalnızca az gelişmiş ülkelerde görülen bir ekonomik durumdur.",
            tfIsTrue = isTrue,
            wordBefore = "Bir işletmenin gelirlerinin giderlerinden fazla olması durumuna",
            wordCorrect = "kâr",
            wordWrong = "zarar",
            wordAfter = "denir.",
            openPrompt = "Fırsat maliyeti kavramını bir öğrencinin zaman veya harçlık bütçesi üzerinden örneklendiriniz.",
            openHint = "Bir seçeneği tercih ettiğinde vazgeçtiğin diğer faydayı anlat."
        )
    }

    private fun wrapInFormat(
        p: ComputedProblem,
        format: QuestionFormat,
        id: String,
        rng: Random,
        examModeLabel: String? = null
    ): QuizQuestion {
        val prefix = if (examModeLabel != null) "[$examModeLabel] " else ""
        return when (format) {
            QuestionFormat.MULTIPLE_CHOICE -> {
                val options = (listOf(p.correctAnswer) + p.distractors.take(3)).shuffled(rng)
                val correctIdx = options.indexOf(p.correctAnswer)
                QuizQuestion.MultipleChoice(
                    id = id,
                    topic = p.topic,
                    prompt = "$prefix${p.questionText}",
                    options = options,
                    correctIndex = correctIdx,
                    explanation = p.explanation
                )
            }
            QuestionFormat.OPEN_ENDED -> QuizQuestion.OpenEnded(
                id = id,
                topic = p.topic,
                prompt = "$prefix${p.openPrompt}",
                hint = p.openHint,
                sampleAnswer = p.explanation,
                explanation = p.explanation
            )
            QuestionFormat.FILL_BLANK -> {
                val cleanBlankAnswer = when {
                    p.blankBefore.contains("Avogadro") -> "Avogadro"
                    p.blankBefore.contains("yükselmesine") -> "enflasyon"
                    p.blankBefore.contains("akım şiddeti ile") -> "direnç"
                    p.blankBefore.contains("kütle ile") -> "ivme"
                    p.blankBefore.contains("kesin olayın") -> "1"
                    else -> p.correctAnswer
                        .replace(" TL", "")
                        .replace(" J", "")
                        .replace(" N", "")
                        .replace(" V", "")
                        .replace(" g", "")
                        .replace("°", "")
                }
                QuizQuestion.FillBlank(
                    id = id,
                    topic = p.topic,
                    sentenceBefore = "$prefix${p.blankBefore}",
                    sentenceAfter = p.blankAfter,
                    correctAnswer = cleanBlankAnswer,
                    acceptedVariants = listOf(p.correctAnswer, cleanBlankAnswer.lowercase()),
                    hint = "İpucu: ${cleanBlankAnswer.take(1)}... (${cleanBlankAnswer.length} karakter)",
                    explanation = p.explanation
                )
            }
            QuestionFormat.TRUE_FALSE -> QuizQuestion.TrueFalse(
                id = id,
                topic = p.topic,
                statement = "$prefix${p.tfStatement}",
                isTrue = p.tfIsTrue,
                explanation = p.explanation
            )
            QuestionFormat.FLASHCARD -> QuizQuestion.Flashcard(
                id = id,
                topic = p.topic,
                frontTerm = p.conceptTerm,
                frontSubtitle = p.topic,
                backDefinition = p.conceptDef,
                backExample = p.explanation,
                explanation = p.conceptDef
            )
            QuestionFormat.WORD_CHOICE -> {
                val swap = rng.nextBoolean()
                QuizQuestion.WordChoice(
                    id = id,
                    topic = p.topic,
                    sentenceBefore = "$prefix${p.wordBefore}",
                    optionA = if (swap) p.wordWrong else p.wordCorrect,
                    optionB = if (swap) p.wordCorrect else p.wordWrong,
                    correctOptionIndex = if (swap) 1 else 0,
                    sentenceAfter = p.wordAfter,
                    explanation = p.explanation
                )
            }
        }
    }
}
