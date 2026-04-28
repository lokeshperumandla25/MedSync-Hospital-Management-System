package com.bank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bank.service.AdminCustomerService;
import com.bank.service.AdminLoanService;
import com.bank.service.CustomerService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {
	
	private final CustomerService customerService;
	private final AdminLoanService adminLoanService;
	private final AdminCustomerService adminCustomerService;
	
	public AdminController(CustomerService customerService , AdminLoanService adminLoanService, AdminCustomerService adminCustomerService) {
		this.customerService= customerService;
		this.adminLoanService = adminLoanService;
		this.adminCustomerService = adminCustomerService;
	}
	
	private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    
    @GetMapping("/admin/login")
    public String adminLoginPage() {
    		return "admin-login";
    }
    
    @PostMapping("/admin/login")
    public String adminLogin(
    		@RequestParam("username") String username, 
    		@RequestParam("password") String password,
    		HttpSession session,
    		Model model) {
    		
    	 if (username == null || password == null ||
                 username.isBlank() || password.isBlank()) {
             model.addAttribute("error", "Username and password are required");
             return "admin-login";
         }
    	 
    	 if (ADMIN_USERNAME.equals(username) &&
    	            ADMIN_PASSWORD.equals(password)) {

    	            session.setAttribute("ADMIN_LOGGED_IN", true);
    	            return "redirect:/admin/dashboard";
    	        }
    	 model.addAttribute("error","Invalid adimn credentials");
    	 return "admin-login";
    }
    
    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session,Model model) {
    	
    		Boolean loggedIn =(Boolean) session.getAttribute("ADMIN_LOGGED_IN");
    		
    		if(loggedIn == null || !loggedIn) {
    			return "redirect:/admin/login";
    		}
    		
    		return "admin-dashboard";
    }
    
    @GetMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }
    
    @GetMapping("/admin/create-customer")
    public String createCustomerPage(HttpSession session) {

        Boolean loggedIn =
                (Boolean) session.getAttribute("ADMIN_LOGGED_IN");

        if (loggedIn == null || !loggedIn) {
            return "redirect:/admin/login";
        }

        return "admin-create-customer";
    }
    
    @PostMapping("/admin/create-customer")
    public String createCustomer(
    		@RequestParam("fullName") String fullName,
    		@RequestParam("email") String email,
    		@RequestParam("mobile") String mobile,
    		HttpSession session,
    		Model model) {
    	
    		Boolean loggedIn =
                (Boolean) session.getAttribute("ADMIN_LOGGED_IN");

        if (loggedIn == null || !loggedIn) {
            return "redirect:/admin/login";
        }
        String result =
                customerService.createCustomer(fullName, email, mobile);

        model.addAttribute("message", result);
        return "admin-create-customer";
    	
    }
    
    @GetMapping("/admin/loans")
    public String viewLoans(HttpSession session, Model model) {
    		
    		Boolean admin = (Boolean)session.getAttribute("ADMIN_LOGGED_IN");
    		
    		if(admin == null || !admin)
    			return "redirect:/admin/login";
    		
    		model.addAttribute("loans",
    					adminLoanService.getPendingLoans());
    		
		return "admin-loans";
    }
    
    @PostMapping("/admin/loan/approve")
    public String approveLoan(
    		@RequestParam("loanId") Long loanId,
    		HttpSession session) {
    	
    		adminLoanService.approveLoan(loanId);
    		return "redirect:/admin/loans";
    	
    }
    
    @PostMapping("/admin/loan/reject")
    public String rejectLoan(
    		@RequestParam("loanId") Long loanId,
    		HttpSession session) {
    	
    		adminLoanService.rejectLoan(loanId);
    		return "redirect:/admin/loans";
    	
    }
    
    @GetMapping("/admin/customers")
    public String viewCustomers(HttpSession session , Model model) {
    		
    		Boolean loggedIn = (Boolean)session.getAttribute("ADMIN_LOGGED_IN");
    		
    		if(loggedIn == null || !loggedIn)
    			return "redirect:/admin/login";
    		
    		model.addAttribute("customers",
    				adminCustomerService.getAllCustomers());
    		
    		return "admin-customers";
    }
    
    @PostMapping("/admin/customer/toggle")
    public String toogleCustomer(
    		@RequestParam("customerId") Long customerId,
    		HttpSession session,
    		Model model) {
    	
    		Boolean loggedIn =(Boolean)session.getAttribute("ADMIN_LOGGED_IN");
    		
    		if(loggedIn == null || !loggedIn)
    			return "redirect:/admin/login";
    		
    		String message = adminCustomerService.toogleCustomerStatus(customerId);
    		
    		model.addAttribute("message",message);
    		
    		return "redirect:/admin/customers";
    }
    
	
	

}
