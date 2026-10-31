package app.pendia.data

/**
 * Wissen und Tipps. Stand und Quellen stehen an jedem Eintrag.
 * Vor jeder neuen Version gegen die aktuellen Fachinformationen prüfen.
 */
data class FaqItem(
    val id: String,
    val titleDe: String,
    val titleEn: String,
    val bodyDe: String,
    val bodyEn: String,
    val sources: List<String>,
    val warning: Boolean = false,
) {
    fun title(de: Boolean) = if (de) titleDe else titleEn
    fun body(de: Boolean) = if (de) bodyDe else bodyEn
}

object Faq {
    const val AS_OF = "31.10.2026"

    private const val SMPC_WEGOVY = "EMA – Fachinformation Wegovy: https://www.ema.europa.eu/en/documents/product-information/wegovy-epar-product-information_de.pdf"
    private const val SMPC_OZEMPIC = "EMA – Fachinformation Ozempic: https://www.ema.europa.eu/en/documents/product-information/ozempic-epar-product-information_de.pdf"
    private const val SMPC_MOUNJARO = "EMA – Fachinformation Mounjaro: https://www.ema.europa.eu/en/documents/product-information/mounjaro-epar-product-information_de.pdf"
    private const val SMPC_TRULICITY = "EMA – Fachinformation Trulicity: https://www.ema.europa.eu/en/documents/product-information/trulicity-epar-product-information_de.pdf"
    private const val SMPC_SAXENDA = "EMA – Fachinformation Saxenda: https://www.ema.europa.eu/en/documents/product-information/saxenda-epar-product-information_de.pdf"
    private const val LILLY_MISSED = "Lilly Medical DE – vergessene Dosis Mounjaro: https://medical.lilly.com/de/products/answers/was-sollte-ein-patient-tun-wenn-eine-dosis-mounjaro-tirzepatid-vergessen-wurde-255542"
    private const val LILLY_PILL = "Lilly Medical DE – Mounjaro und orale Verhütung: https://medical.lilly.com/de/products/answers/ist-bei-der-verwendung-von-mounjaro-tirzepatid-eine-dosisanpassung-der-oralen-kontrazeptiva-erforderlich-174073"
    private const val TGA_PILL = "TGA (Australien) – Verhütung und Mounjaro: https://www.tga.gov.au/news/safety-updates/updated-contraception-advice-mounjaro-tirzepatid"
    private const val PZ_COST = "Pharmazeutische Zeitung – Kostenübernahme: https://www.pharmazeutische-zeitung.de/uebergewichtige-sollen-laut-bmg-weiterhin-selbst-zahlen-145180/"
    private const val STEP1_EXT = "Wilding et al. 2022, Diabetes Obes Metab (STEP-1-Verlängerung): https://doi.org/10.1111/dom.14725"
    private const val EMA_FAKE = "EMA – Warnung vor gefälschten Ozempic-Pens (2023): https://www.ema.europa.eu/en/news/ema-alerts-eu-patients-healthcare-professionals-reports-falsified-ozempic-pens"

    val emergency = FaqItem(
        id = "warning_signs",
        titleDe = "Warnzeichen: Wann sofort zum Arzt?",
        titleEn = "Warning signs: when to get help right away",
        bodyDe = """
Ruf den Notruf 112 oder geh sofort in eine Praxis oder Notaufnahme bei:

• starken, anhaltenden Bauchschmerzen, die in den Rücken ziehen können, mit oder ohne Erbrechen. Das kann eine Entzündung der Bauchspeicheldrüse sein.
• Schmerzen im rechten Oberbauch, Fieber oder gelblicher Haut und Augen. Das kann an der Gallenblase liegen.
• starkem Erbrechen oder Durchfall, bei dem du kaum noch trinken kannst, wenig Urin hast oder dir schwindelig ist.
• Schwellung von Gesicht, Lippen oder Zunge oder Atemnot (allergische Reaktion).
• plötzlichen Sehstörungen, vor allem wenn du eine diabetische Netzhauterkrankung hast.
• Unterzucker, der sich mit Traubenzucker nicht bessert, oder Verwirrtheit.
""".trim(),
        bodyEn = """
Call emergency services (112 in the EU) or see a doctor right away if you have:

• severe, ongoing belly pain that may spread to your back, with or without vomiting. This can be inflammation of the pancreas.
• pain in the upper right belly, fever, or yellow skin or eyes. This can be a gallbladder problem.
• heavy vomiting or diarrhoea where you can barely drink, pass little urine or feel dizzy.
• swelling of the face, lips or tongue, or trouble breathing (allergic reaction).
• sudden changes in your vision, especially if you have diabetic eye disease.
• low blood sugar that does not improve with glucose, or confusion.
""".trim(),
        sources = listOf(SMPC_WEGOVY, SMPC_MOUNJARO, SMPC_OZEMPIC),
        warning = true,
    )

    val items: List<FaqItem> = listOf(
        FaqItem(
            id = "missed_dose",
            titleDe = "Spritze vergessen – was jetzt?",
            titleEn = "Missed a dose – what now?",
            bodyDe = """
Was gilt, steht in der Packungsbeilage deines Medikaments. Kurz zusammengefasst:

• Semaglutid (Ozempic, Wegovy): innerhalb von 5 Tagen nachholen. Sind mehr als 5 Tage vergangen, die Dosis auslassen und am gewohnten Tag weitermachen.
• Tirzepatid (Mounjaro): innerhalb von 4 Tagen (96 Stunden) nachholen, sonst auslassen. Zwischen zwei Spritzen sollen mindestens 3 Tage liegen.
• Dulaglutid (Trulicity): nachholen, wenn bis zur nächsten geplanten Spritze noch mindestens 3 Tage bleiben, sonst auslassen.
• Liraglutid (täglich, z. B. Saxenda): nie doppelt spritzen. Bei Saxenda innerhalb von 12 Stunden nachholen, sonst auslassen.

Hast du mehrere Spritzen hintereinander verpasst, sprich mit deiner Praxis, bevor du weitermachst. Oft wird dann mit einer niedrigeren Dosis neu begonnen.

Pendia rechnet das nicht für dich aus. Die App zeigt nur, wann du zuletzt gespritzt hast.
""".trim(),
            bodyEn = """
Your package leaflet has the rules for your medicine. In short:

• Semaglutide (Ozempic, Wegovy): take it within 5 days. If more than 5 days have passed, skip it and continue on your usual day.
• Tirzepatide (Mounjaro): take it within 4 days (96 hours), otherwise skip it. Keep at least 3 days between two injections.
• Dulaglutide (Trulicity): take it if at least 3 days remain until the next planned dose, otherwise skip it.
• Liraglutide (daily, e.g. Saxenda): never double up. For Saxenda, take it within 12 hours, otherwise skip it.

If you missed several doses in a row, talk to your doctor before you continue. Often you restart on a lower dose.

Pendia does not calculate this for you. It only shows when you last injected.
""".trim(),
            sources = listOf(SMPC_WEGOVY, SMPC_OZEMPIC, LILLY_MISSED, SMPC_TRULICITY, SMPC_SAXENDA),
        ),
        FaqItem(
            id = "titration",
            titleDe = "Wie wird die Dosis normalerweise gesteigert?",
            titleEn = "How is the dose usually increased?",
            bodyDe = """
Die Dosis wird langsam erhöht, damit sich der Magen daran gewöhnt. Wann und wie weit, entscheidet deine Ärztin oder dein Arzt. Das hier ist nur der übliche Plan laut Fachinformation:

• Ozempic: 4 Wochen 0,25 mg, dann 0,5 mg. Bei Bedarf nach jeweils mindestens 4 Wochen 1 mg und 2 mg.
• Wegovy: je 4 Wochen 0,25 → 0,5 → 1 → 1,7 mg, dann 2,4 mg. In der EU ist seit 2026 zusätzlich 7,2 mg zugelassen.
• Mounjaro: 4 Wochen 2,5 mg, dann 5 mg. Danach bei Bedarf in Schritten von 2,5 mg, jeweils nach mindestens 4 Wochen, höchstens 15 mg.
• Saxenda: Start mit 0,6 mg täglich, jede Woche 0,6 mg mehr bis 3,0 mg.

Die Startdosis (z. B. 0,25 mg oder 2,5 mg) dient der Gewöhnung. Sie ist keine Dauerdosis.

Bei starker Übelkeit kann die Steigerung verschoben werden. Das entscheidet deine Praxis.
""".trim(),
            bodyEn = """
The dose goes up slowly so your stomach can adjust. Your doctor decides when and how far. This is only the usual plan from the prescribing information:

• Ozempic: 0.25 mg for 4 weeks, then 0.5 mg. If needed, 1 mg and 2 mg, each after at least 4 weeks.
• Wegovy: 4 weeks each of 0.25 → 0.5 → 1 → 1.7 mg, then 2.4 mg. Since 2026 the EU has also approved 7.2 mg.
• Mounjaro: 2.5 mg for 4 weeks, then 5 mg. After that, if needed, steps of 2.5 mg, each after at least 4 weeks, up to 15 mg.
• Saxenda: start with 0.6 mg daily, add 0.6 mg each week up to 3.0 mg.

The starting dose (e.g. 0.25 mg or 2.5 mg) is there to get used to the medicine. It is not a long-term dose.

With strong nausea, the next step can be delayed. Your doctor decides.
""".trim(),
            sources = listOf(SMPC_OZEMPIC, SMPC_WEGOVY, SMPC_MOUNJARO, SMPC_SAXENDA),
        ),
        FaqItem(
            id = "storage",
            titleDe = "Pen richtig lagern – auch auf Reisen",
            titleEn = "Storing your pen – at home and on the road",
            bodyDe = """
Ungeöffnete Pens gehören in den Kühlschrank (2–8 °C). Nicht einfrieren, nicht direkt an das Kühlelement legen und vor Licht schützen. Ein Pen, der gefroren war, darf nicht mehr verwendet werden.

Wie lange ein Pen ungekühlt oder nach dem ersten Gebrauch hält, hängt vom Produkt ab. Beispiele aus den Fachinformationen:
• Wegovy Einzeldosis-Pen: bis 28 Tage ungekühlt unter 30 °C.
• Ozempic-Pen nach dem ersten Gebrauch: 6 Wochen unter 30 °C oder im Kühlschrank.
• Mounjaro Einzeldosis-Pen: bis 21 Tage ungekühlt unter 30 °C.

Die genaue Zeit für deinen Pen steht in der Packungsbeilage. Trag sie in Pendia unter „Pen“ ein, dann siehst du, wie lange er schon offen ist.

Auf Reisen:
• Pens ins Handgepäck, nicht in den Koffer im Frachtraum. Dort kann es zu kalt werden.
• Bei Hitze eine Kühltasche nehmen, den Pen aber nicht direkt auf den Kühlakku legen.
• Für Flüge hilft eine ärztliche Bescheinigung, auf Englisch.
""".trim(),
            bodyEn = """
Keep unopened pens in the fridge (2–8 °C). Do not freeze them, do not place them right next to the cooling element, and protect them from light. Never use a pen that has been frozen.

How long a pen lasts out of the fridge or after first use depends on the product. Examples from the prescribing information:
• Wegovy single-dose pen: up to 28 days unrefrigerated below 30 °C.
• Ozempic pen after first use: 6 weeks below 30 °C or in the fridge.
• Mounjaro single-dose pen: up to 21 days unrefrigerated below 30 °C.

The exact time for your pen is in the package leaflet. Enter it in Pendia under "Pen" to see how long it has been open.

When travelling:
• Keep pens in your hand luggage, not in checked baggage. The hold can get too cold.
• In hot weather use a cool bag, but do not put the pen directly on the ice pack.
• For flights, a doctor's letter in English helps.
""".trim(),
            sources = listOf(SMPC_WEGOVY, SMPC_OZEMPIC, SMPC_MOUNJARO),
        ),
        FaqItem(
            id = "sites",
            titleDe = "Wo spritzen? Warum die Stelle wechseln?",
            titleEn = "Where to inject and why to rotate",
            bodyDe = """
Gespritzt wird unter die Haut am Bauch, am Oberschenkel oder am Oberarm. Nicht in einen Muskel und nicht in eine Vene.

Wechsle die Stelle jedes Mal. So entstehen weniger Verhärtungen und blaue Flecken, und das Medikament wird gleichmäßiger aufgenommen.

• Am Bauch empfehlen viele Gebrauchsanleitungen etwa 5 cm Abstand zum Bauchnabel.
• Nicht in Narben, blaue Flecken, gerötete oder verhärtete Haut spritzen.
• Am Oberarm geht es allein oft schwer. Hier kann jemand anderes helfen.

Die Körperkarte in Pendia zeigt dir, wo du zuletzt gespritzt hast.
""".trim(),
            bodyEn = """
Inject under the skin of your belly, thigh or upper arm. Not into a muscle and not into a vein.

Change the spot every time. This means fewer lumps and bruises, and the medicine is absorbed more evenly.

• On the belly, many instructions recommend staying about 5 cm (2 inches) away from the belly button.
• Do not inject into scars, bruises, red or hardened skin.
• The upper arm can be hard to reach on your own. Someone else can help.

The body map in Pendia shows where you injected last.
""".trim(),
            sources = listOf(SMPC_WEGOVY, SMPC_MOUNJARO),
        ),
        FaqItem(
            id = "nausea",
            titleDe = "Übelkeit, Verstopfung, Durchfall: was im Alltag hilft",
            titleEn = "Nausea, constipation, diarrhoea: everyday tips",
            bodyDe = """
Magen-Darm-Beschwerden sind die häufigsten Nebenwirkungen. Sie treten vor allem am Anfang und nach einer Dosiserhöhung auf und werden meist mit der Zeit weniger.

Was vielen hilft. Das sind Erfahrungen aus der Praxis, keine Studienergebnisse:
• kleinere Portionen, langsam essen, aufhören, sobald du satt bist
• sehr fettiges, frittiertes oder scharfes Essen meiden
• nach dem Essen nicht gleich hinlegen
• bei Verstopfung: genug trinken, Ballaststoffe nach und nach steigern, bewegen
• bei Übelkeit: trockene, einfache Kost, kleine Schlucke Wasser oder Tee

Trag deine Beschwerden in Pendia ein. Dann kannst du deiner Praxis zeigen, wann sie auftreten und wie stark sie sind.

Hält die Übelkeit an oder kannst du nichts bei dir behalten, sprich mit deiner Praxis. Manchmal hilft es, die nächste Dosiserhöhung zu verschieben.
""".trim(),
            bodyEn = """
Stomach and gut problems are the most common side effects. They mostly happen at the start and after a dose increase, and usually ease over time.

What helps many people. These are everyday experiences, not study results:
• smaller portions, eat slowly, stop as soon as you feel full
• avoid very fatty, fried or spicy food
• do not lie down right after eating
• for constipation: drink enough, increase fibre gradually, keep moving
• for nausea: plain, dry food, small sips of water or tea

Log your symptoms in Pendia so you can show your doctor when they happen and how strong they are.

If nausea continues or you cannot keep anything down, talk to your doctor. Sometimes it helps to delay the next dose increase.
""".trim(),
            sources = listOf(SMPC_MOUNJARO, SMPC_WEGOVY),
        ),
        FaqItem(
            id = "hydration",
            titleDe = "Warum genug trinken so wichtig ist",
            titleEn = "Why drinking enough matters",
            bodyDe = """
Wer weniger Hunger hat, trinkt oft auch weniger. Kommen Erbrechen oder Durchfall dazu, verliert der Körper schnell Flüssigkeit. Laut Fachinformation kann das die Nieren belasten, bis hin zu einer Verschlechterung der Nierenfunktion.

• Trink über den Tag verteilt, auch wenn du keinen Durst hast.
• Bei Durchfall oder Erbrechen besonders auf Flüssigkeit achten.
• Wenig dunkler Urin, Schwindel oder ein trockener Mund sind Zeichen, dass dir Flüssigkeit fehlt.
""".trim(),
            bodyEn = """
When you are less hungry, you often drink less too. With vomiting or diarrhoea, your body loses fluid quickly. According to the prescribing information this can strain the kidneys and even worsen kidney function.

• Drink throughout the day, even if you are not thirsty.
• Pay extra attention to fluids when you have diarrhoea or vomiting.
• Little dark urine, dizziness or a dry mouth are signs that you need more fluid.
""".trim(),
            sources = listOf(SMPC_MOUNJARO, SMPC_WEGOVY),
        ),
        FaqItem(
            id = "protein",
            titleDe = "Eiweiß und Muskeln",
            titleEn = "Protein and muscle",
            bodyDe = """
Beim Abnehmen verliert der Körper nicht nur Fett, sondern auch etwas Muskulatur. Zwei Dinge helfen, Muskeln zu erhalten:

• Eiweiß bei jeder Mahlzeit, z. B. Joghurt, Quark, Eier, Fisch, Hülsenfrüchte, Tofu, mageres Fleisch
• Krafttraining oder Übungen mit dem eigenen Körpergewicht, zwei- bis dreimal pro Woche

Wie viel Eiweiß du brauchst, hängt von dir ab, zum Beispiel von deinen Nieren. Lass dich dazu von deiner Praxis oder einer Ernährungsberatung beraten. Bei Adipositas und Diabetes wird eine Ernährungsberatung oft von der Krankenkasse bezuschusst.
""".trim(),
            bodyEn = """
When you lose weight, your body loses some muscle as well as fat. Two things help to keep muscle:

• protein with every meal, e.g. yoghurt, eggs, fish, beans and lentils, tofu, lean meat
• strength training or bodyweight exercises two to three times a week

How much protein you need depends on you, for example on your kidney health. Ask your doctor or a dietitian.
""".trim(),
            sources = emptyList(),
        ),
        FaqItem(
            id = "contraception",
            titleDe = "Pille und Verhütung",
            titleEn = "The pill and contraception",
            bodyDe = """
Mounjaro (Tirzepatid): Laut EU-Fachinformation sinkt der Spiegel der Pille nach einer Spritze etwas. Das gilt dort aber nicht als klinisch bedeutsam, eine Anpassung ist nicht vorgesehen.
Die Behörden in Großbritannien und Australien raten dagegen: Nach Behandlungsbeginn und nach jeder Dosiserhöhung 4 Wochen lang zusätzlich ein Kondom oder eine andere, nicht orale Methode nutzen.

Unser Rat: Sprich mit deiner Frauenärztin oder deinem Frauenarzt, welche Verhütung für dich passt.

Allgemein gilt bei jeder Pille: Starkes Erbrechen oder Durchfall kurz nach der Einnahme kann den Schutz verringern.

Für Wegovy und Ozempic sieht die Fachinformation keine Wirkungsminderung der Pille vor.
""".trim(),
            bodyEn = """
Mounjaro (tirzepatide): According to the EU prescribing information, a dose slightly lowers the level of the pill in the blood. The EU does not consider this clinically relevant and no adjustment is needed.
UK and Australian regulators advise otherwise: after starting treatment and after each dose increase, use a condom or another non-oral method for 4 weeks.

Our advice: talk to your gynaecologist about which contraception suits you.

For any pill: heavy vomiting or diarrhoea soon after taking it can reduce protection.

For Wegovy and Ozempic the prescribing information does not expect the pill to be less effective.
""".trim(),
            sources = listOf(SMPC_MOUNJARO, LILLY_PILL, TGA_PILL, SMPC_WEGOVY),
        ),
        FaqItem(
            id = "pregnancy",
            titleDe = "Schwangerschaft und Kinderwunsch",
            titleEn = "Pregnancy and planning a family",
            bodyDe = """
GLP-1-Spritzen dürfen in der Schwangerschaft nicht verwendet werden. Frauen, die schwanger werden können, sollen während der Behandlung verhüten.

Bei Semaglutid (Wegovy, Ozempic) empfiehlt die Fachinformation, das Medikament mindestens 2 Monate vor einer geplanten Schwangerschaft abzusetzen. Der Wirkstoff bleibt lange im Körper.

Wenn du schwanger werden möchtest oder es schon bist, sprich so bald wie möglich mit deiner Praxis.
""".trim(),
            bodyEn = """
GLP-1 injections must not be used during pregnancy. Women who can become pregnant should use contraception during treatment.

For semaglutide (Wegovy, Ozempic) the prescribing information recommends stopping at least 2 months before a planned pregnancy, because the medicine stays in the body for a long time.

If you want to become pregnant or already are, talk to your doctor as soon as possible.
""".trim(),
            sources = listOf(SMPC_WEGOVY, SMPC_MOUNJARO),
        ),
        FaqItem(
            id = "surgery",
            titleDe = "OP oder Narkose geplant",
            titleEn = "Planned surgery or anaesthesia",
            bodyDe = """
GLP-1-Medikamente verlangsamen die Magenentleerung. Bei Vollnarkose oder tiefer Sedierung wurde deshalb berichtet, dass Mageninhalt in die Lunge gelangt ist.

Sag deshalb vor jeder Operation, Magen- oder Darmspiegelung und Narkose Bescheid, dass du eine GLP-1-Spritze nutzt, und wann du zuletzt gespritzt hast. Das Narkoseteam entscheidet, was zu tun ist.

Pendia zeigt dir das Datum deiner letzten Spritze auf der Startseite.
""".trim(),
            bodyEn = """
GLP-1 medicines slow down how fast the stomach empties. With general anaesthesia or deep sedation, cases of stomach contents getting into the lungs have been reported.

So before any surgery, endoscopy or anaesthesia, tell the team that you use a GLP-1 injection and when you last injected. They will decide what to do.

Pendia shows the date of your last injection on the home screen.
""".trim(),
            sources = listOf(SMPC_WEGOVY, SMPC_OZEMPIC, SMPC_MOUNJARO),
        ),
        FaqItem(
            id = "hypo",
            titleDe = "Unterzucker bei Diabetes",
            titleEn = "Low blood sugar with diabetes",
            bodyDe = """
GLP-1-Spritzen allein führen selten zu Unterzucker. Das Risiko steigt, wenn du zusätzlich Insulin oder Sulfonylharnstoffe (z. B. Glimepirid) nimmst. Dann senkt deine Praxis manchmal deren Dosis.

Typische Zeichen: Zittern, Schwitzen, Herzklopfen, Heißhunger, Unruhe, Konzentrationsprobleme, Verwirrtheit.

Was tun: Blutzucker messen, wenn möglich. Schnell wirkenden Zucker nehmen, z. B. Traubenzucker oder Saft. Nach 15 Minuten erneut messen. Sag deiner Praxis Bescheid, wenn das öfter passiert.

Im Diabetes-Modus kannst du in Pendia festhalten, wann ein Unterzucker war.
""".trim(),
            bodyEn = """
GLP-1 injections on their own rarely cause low blood sugar. The risk rises if you also take insulin or sulfonylureas (e.g. glimepiride). Your doctor may then lower their dose.

Typical signs: shaking, sweating, racing heart, sudden hunger, restlessness, trouble concentrating, confusion.

What to do: check your blood sugar if you can. Take fast-acting sugar, e.g. glucose tablets or juice. Check again after 15 minutes. Tell your doctor if it happens more often.

In diabetes mode you can record low blood sugar episodes in Pendia.
""".trim(),
            sources = listOf(SMPC_MOUNJARO, SMPC_OZEMPIC),
        ),
        FaqItem(
            id = "alcohol",
            titleDe = "Alkohol",
            titleEn = "Alcohol",
            bodyDe = """
Die Fachinformationen enthalten keine eigene Regel zu Alkohol. Zwei Dinge solltest du trotzdem wissen:

• Alkohol kann Übelkeit und Magenbeschwerden verstärken.
• Wer zusätzlich Insulin oder Sulfonylharnstoffe nimmt, hat mit Alkohol ein höheres Risiko für Unterzucker.

Sprich mit deiner Praxis, was für dich in Ordnung ist.
""".trim(),
            bodyEn = """
The prescribing information has no specific rule on alcohol. Two things are still worth knowing:

• Alcohol can make nausea and stomach problems worse.
• If you also take insulin or sulfonylureas, alcohol raises the risk of low blood sugar.

Ask your doctor what is fine for you.
""".trim(),
            sources = emptyList(),
        ),
        FaqItem(
            id = "stopping",
            titleDe = "Absetzen und Gewicht danach",
            titleEn = "Stopping and your weight afterwards",
            bodyDe = """
Setz das Medikament nicht auf eigene Faust ab. Sprich vorher mit deiner Praxis.

Studien zeigen, dass nach dem Absetzen oft wieder Gewicht zunimmt. In einer Folgestudie zu Semaglutid kam im Jahr nach dem Absetzen im Schnitt etwa zwei Drittel des verlorenen Gewichts zurück.

Was hilft, das Gewicht zu halten: Gewohnheiten, die du während der Behandlung aufbaust, also regelmäßige Mahlzeiten, Eiweiß, Bewegung und Krafttraining. Pendia hilft dir, deinen Verlauf auch nach dem Absetzen weiter zu beobachten.
""".trim(),
            bodyEn = """
Do not stop the medicine on your own. Talk to your doctor first.

Studies show that weight often comes back after stopping. In a follow-up study on semaglutide, people regained about two thirds of the lost weight on average within a year of stopping.

What helps to keep the weight off: the habits you build during treatment, such as regular meals, protein, being active and strength training. Pendia lets you keep tracking your progress after you stop.
""".trim(),
            sources = listOf(STEP1_EXT),
        ),
        FaqItem(
            id = "fakes",
            titleDe = "Fälschungen vermeiden",
            titleEn = "Avoiding fake pens",
            bodyDe = """
GLP-1-Medikamente werden oft gefälscht. Die EMA hat schon 2023 vor gefälschten Ozempic-Pens in der EU gewarnt.

• Kauf nur mit Rezept in einer Apotheke oder einer registrierten Versandapotheke.
• Finger weg von Angeboten über Social Media, Messenger oder Webseiten ohne Rezept.
• Ist die Packung beschädigt, sieht der Pen anders aus als sonst oder fehlt die Beschriftung: nicht verwenden, sondern zurück in die Apotheke bringen.
""".trim(),
            bodyEn = """
GLP-1 medicines are often counterfeited. The EMA warned about fake Ozempic pens in the EU back in 2023.

• Only buy with a prescription from a pharmacy or a registered online pharmacy.
• Stay away from offers on social media, messengers or websites without a prescription.
• If the box is damaged, the pen looks different from usual or labels are missing: do not use it, take it back to the pharmacy.
""".trim(),
            sources = listOf(EMA_FAKE),
        ),
        FaqItem(
            id = "costs",
            titleDe = "Wer zahlt? (Deutschland)",
            titleEn = "Who pays? (Germany)",
            bodyDe = """
Stand Oktober 2026, bitte bei deiner Krankenkasse nachfragen:

• Typ-2-Diabetes: Die gesetzliche Krankenkasse übernimmt die Kosten für zugelassene Diabetes-Medikamente wie Ozempic oder Mounjaro, wenn sie ärztlich verordnet sind. Es fällt die übliche Zuzahlung an.
• Nur Übergewicht oder Adipositas: Medikamente zum Abnehmen sind gesetzlich von der Erstattung ausgeschlossen (§ 34 SGB V). Du zahlst sie selbst, mit einem Privatrezept. Gerichte haben das bisher bestätigt.
• Private Krankenversicherung: hängt vom Tarif. Vorher schriftlich nachfragen.
""".trim(),
            bodyEn = """
For Germany, as of October 2026. Please check with your health insurer:

• Type 2 diabetes: statutory health insurance covers approved diabetes medicines such as Ozempic or Mounjaro when prescribed. The usual co-payment applies.
• Overweight or obesity only: weight-loss medicines are excluded from coverage by law (§ 34 SGB V). You pay yourself, with a private prescription. Courts have upheld this so far.
• Private insurance: depends on your plan. Ask in writing first.

In other countries the rules differ.
""".trim(),
            sources = listOf(PZ_COST),
        ),
        FaqItem(
            id = "about",
            titleDe = "Was Pendia kann und was nicht",
            titleEn = "What Pendia does and does not do",
            bodyDe = """
Pendia ist ein Tagebuch. Du hältst fest, wann du gespritzt hast, wo, mit welcher Dosis, wie es dir geht und wie sich dein Gewicht entwickelt. Die App erinnert dich an deinen Spritztag und erstellt einen Bericht für deine Praxis.

Pendia berechnet keine Dosis, gibt keine Empfehlung zur Behandlung und ersetzt keine ärztliche Beratung. Halte dich immer an die Anweisungen deiner Praxis und an die Packungsbeilage.

Deine Daten bleiben auf deinem Handy. Es gibt kein Konto, keine Cloud, keine Werbung und kein Tracking. Die App braucht nicht einmal eine Internetverbindung. Sichern kannst du deine Daten selbst über „Einstellungen → Daten exportieren“.

Markennamen wie Ozempic, Wegovy, Mounjaro, Trulicity, Saxenda und Victoza gehören ihren Inhabern. Pendia ist unabhängig und mit keinem Hersteller verbunden.
""".trim(),
            bodyEn = """
Pendia is a diary. You record when and where you injected, which dose, how you feel and how your weight changes. The app reminds you of your injection day and creates a report for your doctor.

Pendia does not calculate doses, does not give treatment advice and does not replace medical advice. Always follow your doctor's instructions and the package leaflet.

Your data stays on your phone. There is no account, no cloud, no ads and no tracking. The app does not even need an internet connection. You can back up your data yourself under "Settings → Export data".

Brand names such as Ozempic, Wegovy, Mounjaro, Trulicity, Saxenda and Victoza belong to their owners. Pendia is independent and not affiliated with any manufacturer.
""".trim(),
            sources = emptyList(),
        ),
    )
}
