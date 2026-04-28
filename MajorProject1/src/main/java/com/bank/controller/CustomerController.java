package com.bank.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bank.dao.LoanRepository;
import com.bank.model.Customer;
import com.bank.model.Loan;
import com.bank.model.Transcation;
import com.bank.service.CustomerLoginService;
import com.bank.service.DashBoardService;
import com.bank.service.FundTransferService;
import com.bank.service.LoanService;
import com.bank.service.OtpService;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transaction;

@Controller
public class CustomerController {
	
	private final CustomerLoginService customerLoginService;
	private final OtpService otpService;
	private final DashBoardService dashboardService;
	private final FundTransferService fundTransferService;
	private final LoanService loanService;
	private final LoanRepository loanRepository;
	
	public CustomerController(CustomerLoginService customerLoginService,OtpService otpService,DashBoardService dashboardService,FundTransferService fundTransferService,LoanService loanService, LoanRepository loanRepository) {
		this.customerLoginService= customerLoginService;
		this.otpService= otpService;
		this.dashboardService = dashboardService;
		this.fundTransferService = fundTransferService;
		this.loanService = loanService;
		this.loanRepository =  loanRepository;
	}
	
	@GetMapping("/customer/login")
	public String customerLoginPage() {
		return "customer-login";
	}
	
	@PostMapping("/customer/login")
	public String customerLogin(
			@RequestParam(name = "accountNumber") String accountNumber,
			@RequestParam(name = "password") String password,
			HttpSession session,
			Model model) {
		
		String result = customerLoginService.validateLogin(accountNumber,password);
		
		if("RESET_REQUIRED".equals(result)) {
			session.setAttribute("RESET_ACC", accountNumber);
			return "redirect:/customer/reset-password";
		}
		
		if("SUCCESS".equals(result)){
			session.setAttribute("OTP_ACC", accountNumber);
			otpService.generateOtp(accountNumber);
			return "redirect:/customer/otp";
		}
		
		model.addAttribute("error",result);
		return "customer-login";	
	}
	
	@GetMapping("/customer/reset-password")
	public String resetPage() {
		return "reset-password";
	}
	
	@PostMapping("/customer/reset-password")
	public String resetPassword(
			@RequestParam(name = "newPassword") String newPassword,
			HttpSession session,
			Model model) {
		
		String accountNumber = (String)session.getAttribute("RESET_ACC");
		
		String result = customerLoginService.resetPassword(accountNumber,newPassword);
		
		if("SUCCESS".equals(result)) {
			session.removeAttribute("RESET_ACC");
			return "redirect:/customer/login";
		}
		
		model.addAttribute("error",result);
		return "reset-password";
	}
	
	@GetMapping("/customer/otp")
	public String otpPage() {
		return "customer-otp";
	}
	
	@PostMapping("/customer/otp")
	public String verifyOtp(
			@RequestParam(name = "otp") String otp,
			HttpSession session,
			Model model) {
		
		String accountNumber = (String)session.getAttribute("OTP_ACC");
//		System.out.println(accountNumber);
		
		if(accountNumber == null) {
			return "redirect:/customer/login";
		}
		
		boolean valid = otpService.validateOtp(accountNumber,otp);
		
		if(!valid) {
			model.addAttribute("error","Invalid or expired OTP");
			return "customer-otp";
		}
		
		session.removeAttribute("OTP_ACC");
		session.setAttribute("CUSTOMER_LOGGED_IN", true);
		session.setAttribute("LOGGED_IN_ACC", accountNumber);
	
		return "redirect:/customer/dashboard";
		
	}
	
	@GetMapping("/customer/dashboard")
	public String customerDashBoard(
			HttpSession session,
			Model model) {
		
		Boolean loggedIn = (Boolean)session.getAttribute("CUSTOMER_LOGGED_IN");
		
		if(loggedIn == null || !loggedIn) {
			return "redirect:/customer/login";
		}
		
        String accountNumber =
                (String) session.getAttribute("LOGGED_IN_ACC");
        
        Customer customer =
                dashboardService.getCustomerByAccount(accountNumber);
        System.out.println(customer);
        
		if(customer == null) {
			return "redirect:/customer/login";
		}
		
		model.addAttribute("customer",customer);
		return "customer-dashboard";
	}
	
	@GetMapping("/customer/transfer")
	public String transferPage(HttpSession session) {
		
		Boolean loggedIn =(Boolean) session.getAttribute("CUSTOMER_LOGGED_IN");
		
		if(loggedIn == null || !loggedIn) {
			return "redirect:/customer/login";
		}
		
		return "fund-transfer";
	}
	
	@PostMapping("/customer/transfer")
	public String fundTransfer(
			@RequestParam("toAccount") String toAccount,
			@RequestParam("amount") double amount,
			HttpSession session,
			Model model) {
		
		 Boolean loggedIn =
	                (Boolean) session.getAttribute("CUSTOMER_LOGGED_IN");

		if(loggedIn == null || !loggedIn) {
			return "redirect:/customer/login";
		}
		
		String fromAccount =
                (String) session.getAttribute("LOGGED_IN_ACC");
		
		String result =
				fundTransferService.transfer(fromAccount, toAccount, amount);
		
		model.addAttribute("message",result);
		
		return "fund-transfer";	
	}
	
	@GetMapping("/customer/loan/apply")
	public String applyLoanPage(HttpSession session) {
		
		 Boolean loggedIn = (Boolean)session.getAttribute("CUSTOMER_LOGGED_IN");
		 
		 if(loggedIn == null || !loggedIn) {
			 return "redirect:/customer/login";
		 }
		 
		 return "customer-loan-apply";
		 
	}
	
	@GetMapping("/customer/logout")
    public String logout(HttpSession session) {

        if (session != null) {
            session.invalidate(); 
        }

        return "redirect:/customer/login";
    }
	
	@PostMapping("/customer/loan/apply")
	public String applyLoan(
			@RequestParam("loanType") String loanType,
			@RequestParam("amount") double amount,
			@RequestParam("tenure") int tenure,
			HttpSession session,
			Model model) {
		
		String accNo = (String)session.getAttribute("LOGGED_IN_ACC");
		System.out.println(accNo);
		
		String result = loanService.applyLoan(accNo,loanType,amount,tenure);
		
		model.addAttribute("message",result);
		return "customer-loan-apply";
		
	}
	
	@GetMapping("/customer/loans")
	public String viewLoans(HttpSession session,Model model) {
		
		String accNo = (String)session.getAttribute("LOGGED_IN_ACC");
		System.out.println(accNo);
		
		List<Loan> loan = loanRepository.findByAccountNumber(accNo);
		System.out.println(loan);
		
		model.addAttribute("loans", loan);
		return "customer-loan";
	}
	
	@GetMapping("/customer/statement")
	public String miniStatement(HttpSession session,Model model) {
		
		Boolean loggedIn = (Boolean)session.getAttribute("CUSTOMER_LOGGED_IN");
		
		if(loggedIn == null || !loggedIn) {
			return "redirect:/customer/login";
		}
		
		String accNo = (String)session.getAttribute("LOGGED_IN_ACC");
		
		List<Transcation> transactions = dashboardService.getMiniStatement(accNo);
		
		for(Transcation tx : transactions) {
			if(accNo.equals(tx.getFromAccount())) {
				tx.setType("DEBIT");
			} else {
				tx.setType("CREDIT");
			}
		}
		
		model.addAttribute("transactions", transactions);
		
		return "customer-statement";
	}
	
	@GetMapping("/customer/statement/filter")
    public String filterStatement(
            @RequestParam(required = false) Integer month,
            @RequestParam Integer year,
            HttpSession session,
            Model model) {

        Boolean loggedIn =
                (Boolean) session.getAttribute("CUSTOMER_LOGGED_IN");

        if (loggedIn == null || !loggedIn)
            return "redirect:/customer/login";

        String accNo =
                (String) session.getAttribute("LOGGED_IN_ACC");

        List<Transaction> list =
                dashboardService.getFilteredStatement(accNo, month, year);

        model.addAttribute("transactions", list);
        model.addAttribute("month", month);
        model.addAttribute("year", year);

        return "customer-statement";
    }
	
	
	
	
	
	

}
