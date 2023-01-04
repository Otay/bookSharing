package com.udemy.demo.borrow;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.udemy.demo.book.Book;
import com.udemy.demo.book.BookController;
import com.udemy.demo.book.BookRepository;
import com.udemy.demo.book.BookStatus;
import com.udemy.demo.user.UserInfo;
import com.udemy.demo.user.UserRepository;

@RestController
public class BorrowController {
	@Autowired
	BorrowRepository borrowRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	BookRepository bookRepository;
	@Autowired
	BookController bookController;
	
	@GetMapping(value ="/borrows")
	public ResponseEntity getMyBorrows(Principal principal) {
		
		List<Borrow> myBorrows = borrowRepository.findByBorrowerId(1);
		
		return new ResponseEntity(myBorrows, HttpStatus.OK);
		
	}
	
	@PostMapping("/borrows/{bookId}")
	public ResponseEntity createBorrow(@PathVariable("bookId") String bookId, Principal principal) {
		//Integer userId = bookController.getUserId(principal);
		Optional<UserInfo> user = userRepository.findById(1);
		Optional<Book> book = bookRepository.findById(Integer.valueOf(bookId));
		if(user.isPresent() && book.isPresent() && book.get().getBookStatus().equals(BookStatus.FREE)) {
			Borrow borrow = new Borrow();
			borrow.setAskDate(LocalDate.now());
			borrow.setBook(book.get());
			borrow.setBorrower(user.get());
			borrow.setLender(book.get().getUser());
			
			borrowRepository.save(borrow);
			
			book.get().setBookStatus(BookStatus.BORROWED);
			bookRepository.save(book.get());
			
			return new ResponseEntity(borrow, HttpStatus.CREATED);
		}
		
		return new ResponseEntity(HttpStatus.NO_CONTENT);

	}
	
	@DeleteMapping("/borrows/{borrowId}")
	public ResponseEntity deleteBorrow(@PathVariable("borrowId") String borrowId) {
		Optional<Borrow> borrowOp = borrowRepository.findById(Integer.valueOf(borrowId));
		if(borrowOp.isPresent()) {
			Borrow borrow = borrowOp.get();
			borrow.setCloseDate(LocalDate.now());
			
			borrowRepository.save(borrow);
			
			Book book = borrow.getBook();
			book.setBookStatus(BookStatus.FREE);
			
			bookRepository.save(book);
		}
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

}
