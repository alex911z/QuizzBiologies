package com.example.quizz_biologies.controller;

import com.example.quizz_biologies.model.QuizResult;
import com.example.quizz_biologies.repository.QuizResultRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/quiz/simple")
public class SimpleQuizController {

    private final QuizResultRepository quizResultRepository;

    public SimpleQuizController(QuizResultRepository quizResultRepository) {
        this.quizResultRepository = quizResultRepository;
    }

    /**
     * Întrebări în memoria aplicației, fiecare cu 4 variante și indexul răspunsului corect.
     */
    private List<Question> questions() {
        List<Question> q = new ArrayList<>();
        q.add(new Question("Care este unitatea structurală și funcțională a vieții?", List.of("Țesutul", "Celula", "Organul", "Sistemul"), 1));
        q.add(new Question("Ce organit produce energie în celula eucariotă?", List.of("Aparatul Golgi", "Mitocondria", "Ribozomul", "Lizosomul"), 1));
        q.add(new Question("Unde are loc fotosinteza?", List.of("Mitocondrii", "Cloroplaste", "Reticul endoplasmatic", "Citoplasmă"), 1));
        q.add(new Question("Ce legătură unește bazele azotate în ADN?", List.of("Legătură ionică", "Legătură peptidică", "Legătură de hidrogen", "Legătură disulfidică"), 2));
        q.add(new Question("Ce tip de celule produc anticorpi?", List.of("Celule T citotoxice", "Celule NK", "Plasmocite (celule B diferențiate)", "Neutrofile"), 2));
        q.add(new Question("Cum se numește procesul de diviziune celulară somatică?", List.of("Meioză", "Metabolism", "Mitoză", "Transcripție"), 2));
        q.add(new Question("Care hormon scade glicemia?", List.of("Insulina", "Glucagonul", "Adrenalina", "Cortizolul"), 0));
        q.add(new Question("Ce structură reglează schimbul de gaze la plante?", List.of("Rădăcina", "Stomatele", "Cambiumul", "Floema"), 1));
        q.add(new Question("Care este faza din fotosinteză în care se produce glucoza?", List.of("Faza luminoasă", "Ciclul Calvin (faza întunecată)", "Respirația celulară", "Fermentația"), 1));
        q.add(new Question("Ce legătură unește aminoacizii într-o proteină?", List.of("Legătură peptidică", "Legătură de hidrogen", "Legătură ionică", "Legătură fosfodiesterică"), 0));
        return q;
    }

    @GetMapping
    public String showQuiz(Model model) {
        model.addAttribute("questions", questions());
        return "simple-quiz";
    }

    @PostMapping
    public String submitQuiz(@RequestParam Map<String, String> form, Model model, Authentication authentication) {
        List<Question> qs = questions();
        int total = qs.size(); // 10
        int correct = 0;
        for (int i = 0; i < qs.size(); i++) {
            String key = "q" + i;
            String answerIdx = form.get(key);
            if (answerIdx != null) {
                try {
                    int idx = Integer.parseInt(answerIdx);
                    if (idx == qs.get(i).correctIndex()) {
                        correct++;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        // Nota simplă 1..10 proporțional cu numărul de răspunsuri corecte
        double grade = Math.round((correct / (double) total) * 9 + 1);

        // Salvează rezultatul în DB pentru utilizatorul curent
        if (authentication != null) {
            QuizResult result = new QuizResult();
            result.setUsername(authentication.getName());
            result.setTestName("Quiz simplu biologie");
            result.setScore((int) grade);
            quizResultRepository.save(result);
        }

        model.addAttribute("questions", qs);
        model.addAttribute("submitted", true);
        model.addAttribute("correct", correct);
        model.addAttribute("total", total);
        model.addAttribute("grade", (int) grade);
        return "simple-quiz";
    }

    public record Question(String text, List<String> options, int correctIndex) {
    }
}
