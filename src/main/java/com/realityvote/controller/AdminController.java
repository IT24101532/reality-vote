package com.realityvote.controller;

import com.realityvote.model.*;
import com.realityvote.model.enums.Role;
import com.realityvote.repository.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final VoteRepository voteRepo;
    private final AdvertisementRepository adRepo;
    private final FaqRepository faqRepo;
    private final ProgramRepository programRepo;
    private final ContestantRepository contestantRepo;
    private final UserAccountRepository userRepo;
    private final WhatsNewRepository whatsNewRepo;
    private final BCryptPasswordEncoder enc;
    private final FaqQueryRepository faqQueryRepo;

    public AdminController(AdvertisementRepository adRepo,
                           FaqRepository faqRepo,
                           ProgramRepository programRepo,
                           ContestantRepository contestantRepo,
                           UserAccountRepository userRepo,
                           WhatsNewRepository whatsNewRepo,
                           VoteRepository voteRepo,
                           FaqQueryRepository faqQueryRepo,
                           BCryptPasswordEncoder enc) {
        this.adRepo = adRepo;
        this.faqRepo = faqRepo;
        this.programRepo = programRepo;
        this.contestantRepo = contestantRepo;
        this.userRepo = userRepo;
        this.whatsNewRepo = whatsNewRepo;
        this.voteRepo = voteRepo;
        this.faqQueryRepo = faqQueryRepo;
        this.enc = enc;
    }

    // ----------------------- Dashboard -----------------------
    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    // ----------------------- FAQs -----------------------
    @GetMapping("/faqs")
    public String faqs(Model model) {
        model.addAttribute("faq", new Faq());
        model.addAttribute("items", faqRepo.findAll());
        model.addAttribute("queries", faqQueryRepo.findAll());
        return "admin/faqs";
    }

    @PostMapping("/faqs/save")
    public String saveFaq(@ModelAttribute Faq faq) {
        faqRepo.save(faq);
        return "redirect:/admin/faqs";
    }

    @GetMapping("/faqs/edit/{id}")
    public String editFaq(@PathVariable Long id, Model model) {
        Faq existing = faqRepo.findById(id).orElse(null);
        if (existing == null) return "redirect:/admin/faqs";

        model.addAttribute("faq", existing);
        model.addAttribute("items", faqRepo.findAll());
        model.addAttribute("queries", faqQueryRepo.findAll());
        return "admin/faqs";
    }

    @PostMapping("/faqs/delete/{id}")
    public String deleteFaq(@PathVariable Long id) {
        faqRepo.deleteById(id);
        return "redirect:/admin/faqs";
    }

    @PostMapping("/faqs/promote/{id}")
    public String promoteQuery(@PathVariable Long id) {
        FaqQuery q = faqQueryRepo.findById(id).orElse(null);
        if (q != null) {
            Faq f = new Faq();
            f.setQuestion(q.getQuestion());
            f.setAnswer("Pending admin response...");
            faqRepo.save(f);
            faqQueryRepo.delete(q);
        }
        return "redirect:/admin/faqs";
    }

    // ----------------------- Programs -----------------------
    @GetMapping("/programs")
    public String programs(Model model) {
        model.addAttribute("items", programRepo.findAll());
        model.addAttribute("program", new Program());
        return "admin/programs";
    }

    @PostMapping("/programs/save")
    public String saveProgram(Program program) {
        programRepo.save(program);
        return "redirect:/admin/programs";
    }

    @GetMapping("/programs/delete/{id}")
    public String deleteProgram(@PathVariable Long id) {
        programRepo.deleteById(id);
        return "redirect:/admin/programs";
    }

    @GetMapping("/programs/toggle/{id}")
    public String toggleProgramStatus(@PathVariable Long id) {
        Program p = programRepo.findById(id).orElse(null);
        if (p != null) {
            p.setActive(!p.isActive()); // ✅ Toggle true/false
            programRepo.save(p);
        }
        return "redirect:/admin/programs";
    }


    @GetMapping("/shows")
    public String showsRedirect() {
        return "redirect:/admin/programs";
    }

    // ----------------------- Contestants Management -----------------------

    // ✅ Manage Contestants Page
    @GetMapping("/contestants")
    public String manageContestants(Model model,
                                    @RequestParam(value = "success", required = false) String success) {
        model.addAttribute("contestants", contestantRepo.findAll());
        model.addAttribute("programs", programRepo.findAll());
        model.addAttribute("success", success);
        return "admin/manage-contestants";
    }

    // ✅ Create Contestant (Form)
    @GetMapping("/contestants/create")
    public String createContestantForm(Model model) {
        model.addAttribute("newContestant", new Contestant());
        return "admin/create-contestant";
    }

    // ✅ Save Contestant (without assigning shows)
    @PostMapping("/contestants/create/save")
    public String createContestant(
            @RequestParam String name,
            @RequestParam(required = false) String bio,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // ✅ Check if username or email already exist
            if (userRepo.findByUsername(username).isPresent()) {
                model.addAttribute("error", "⚠️ Username already exists. Please choose another one.");
                return "admin/create-contestant";
            }
            if (userRepo.findByEmail(email).isPresent()) {
                model.addAttribute("error", "⚠️ Email already exists. Please use a different email.");
                return "admin/create-contestant";
            }

            // ✅ Create user account
            UserAccount account = new UserAccount();
            account.setUsername(username);
            account.setPasswordHash(enc.encode(password));
            account.setEmail(email);
            account.setRole(Role.CONTESTANT);
            account.setEnabled(true);
            account.setFullName(name);
            userRepo.save(account);

            // ✅ Create contestant
            Contestant c = new Contestant();
            c.setName(name);
            c.setBio(bio);
            c.setAccount(account);
            contestantRepo.save(c);

            // ✅ Redirect with success
            redirectAttributes.addFlashAttribute("success", "🎉 Contestant created successfully!");

            return "redirect:/admin/contestants";

        } catch (Exception e) {
            model.addAttribute("error", "⚠️ Error while saving contestant: " + e.getMessage());
            return "admin/create-contestant";
        }
    }


    // ✅ Assign Shows to an existing contestant
    @GetMapping("/contestants/assign/{id}")
    public String assignShows(@PathVariable Long id, Model model) {
        Contestant c = contestantRepo.findById(id).orElse(null);
        if (c == null) return "redirect:/admin/contestants";
        model.addAttribute("contestant", c);
        model.addAttribute("programs", programRepo.findAll());
        return "admin/assign-shows";
    }

    @PostMapping("/contestants/assign/save/{id}")
    public String assignShowsSave(@PathVariable Long id,
                                  @RequestParam(required = false) List<Long> programIds) {
        Contestant c = contestantRepo.findById(id).orElse(null);
        if (c != null) {
            List<Program> programs = (programIds != null) ? programRepo.findAllById(programIds) : new ArrayList<>();
            c.setPrograms(programs);
            contestantRepo.save(c);
        }
        return "redirect:/admin/contestants?success=Shows+updated+successfully";
    }

    @GetMapping("/contestants/delete/{id}")
    public String deleteContestant(@PathVariable Long id) {
        Contestant contestant = contestantRepo.findById(id).orElse(null);
        if (contestant != null) {
            UserAccount account = contestant.getAccount();
            contestantRepo.delete(contestant);
            if (account != null) {
                userRepo.delete(account);
            }
        }
        return "redirect:/admin/contestants";
    }


    // ----------------------- What's New -----------------------
    @GetMapping("/whats-new")
    public String manageWhatsNew(Model model) {
        model.addAttribute("posts", whatsNewRepo.findAll());
        model.addAttribute("post", new WhatsNew());
        return "admin/whats-new";
    }

    @PostMapping("/whats-new/save")
    public String saveWhatsNew(@ModelAttribute WhatsNew post) {
        whatsNewRepo.save(post);
        return "redirect:/admin/whats-new";
    }

    @GetMapping("/whats-new/delete/{id}")
    public String deleteWhatsNew(@PathVariable Long id) {
        whatsNewRepo.deleteById(id);
        return "redirect:/admin/whats-new";
    }
    @GetMapping("/whats-new/edit/{id}")
    public String editWhatsNew(@PathVariable Long id, Model model) {
        WhatsNew existing = whatsNewRepo.findById(id).orElse(null);
        if (existing == null) return "redirect:/admin/whats-new";

        model.addAttribute("post", existing);
        model.addAttribute("posts", whatsNewRepo.findAll());
        return "admin/whats-new";
    }


    // ----------------------- Reports -----------------------
    @GetMapping("/reports")
    public String showReports(Model model) {
        List<Object[]> reports = voteRepo.getVotingReport();
        model.addAttribute("reports", reports);
        return "admin/reports";
    }

    @GetMapping("/reports/download")
    public void downloadReport(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=voting_report.csv");

        List<Object[]> reports = voteRepo.getVotingReport();
        PrintWriter writer = response.getWriter();
        writer.println("Viewer Email,Viewer Username,Contestant,Program");
        for (Object[] row : reports) {
            writer.println(row[0] + "," + row[1] + "," + row[2] + "," + row[3]);
        }
        writer.flush();
        writer.close();
    }
}
