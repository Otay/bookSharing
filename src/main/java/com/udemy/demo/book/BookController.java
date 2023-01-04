package com.udemy.demo.book;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.udemy.demo.borrow.Borrow;
import com.udemy.demo.borrow.BorrowRepository;
import com.udemy.demo.user.UserInfo;
import com.udemy.demo.user.UserRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RequestMapping("/v1")
@RestController
@SecurityRequirement(name = "bearerAuth")
public class BookController {

	@Autowired
	BookRepository bookRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	BorrowRepository borrowRepository;

	@GetMapping(value = "/books")
	public ResponseEntity<List<Book>> listBooks(@RequestParam(required = false) BookStatus status,
			Principal principal) {

		Integer userId = getUserId(principal);
		// my books
		List<Book> myBooks = bookRepository.findByUserIdAndDeletedFalse(userId);

		// available books
		if (status != null && status.equals(BookStatus.FREE))
			myBooks = bookRepository.findByBookStatusAndUserIdNotAndDeletedFalse(status, userId);

		return new ResponseEntity<List<Book>>(myBooks, HttpStatus.OK);
	}

	public Integer getUserId(Principal principal) {
		if (!(principal instanceof UsernamePasswordAuthenticationToken)) {
			throw new RuntimeException("user not found!");
		}

		UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) principal;

		UserInfo existingUser = userRepository.findOneByEmail(user.getName());

		return existingUser.getId();

	}

	@GetMapping(value = "/categories")
	public ResponseEntity<List<BookCategory>> listCategories() {
		return new ResponseEntity(categoryRepository.findAll(), HttpStatus.OK);
	}

	@PostMapping(value = "/books")
	public ResponseEntity addBook(@RequestBody @Valid Book book, Principal principal) {
		Integer userId = getUserId(principal);

		Optional<UserInfo> user = userRepository.findById(userId);
		Optional<BookCategory> category = categoryRepository.findById(book.getCategoryId());
		if (category.isPresent()) {
			book.setCategory(category.get());
		} else {
			return new ResponseEntity("bad category received!", HttpStatus.BAD_REQUEST);
		}

		if (user.isPresent()) {
			book.setUser(user.get());
		} else {
			return new ResponseEntity("bad user received!", HttpStatus.BAD_REQUEST);
		}
		book.setDeleted(false);
		book.setBookStatus(BookStatus.FREE);
		bookRepository.save(book);
		return new ResponseEntity(book, HttpStatus.CREATED);
	}

	@DeleteMapping(value = "/books/{bookId}")
	public ResponseEntity deleteBook(@PathVariable("bookId") Integer bookId) {
		Optional<Book> book = bookRepository.findById(bookId);
		if (!book.isPresent()) {
			return new ResponseEntity("bad book id received!", HttpStatus.BAD_REQUEST);
		}

		List<Borrow> borrows = borrowRepository.findByBookId(bookId);
		for (Borrow borrow : borrows) {
			if (borrow.getCloseDate() == null) {
				UserInfo user = borrow.getBorrower();
				return new ResponseEntity("bad book id received!", HttpStatus.CONFLICT);
			}
		}
		book.get().setDeleted(true);
		bookRepository.save(book.get());
		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@PutMapping(value = "/books/{bookId}")
	public ResponseEntity updateBook(@PathVariable("bookId") String bookId, @RequestBody Book book) {

		Optional<Book> bookToUpdate = bookRepository.findById(Integer.valueOf(bookId));
		if (!bookToUpdate.isPresent()) {
			return new ResponseEntity("book not present!", HttpStatus.BAD_REQUEST);
		}
		Book bookToSave = bookToUpdate.get();

		Optional<BookCategory> category = categoryRepository.findById(book.getCategoryId());
		bookToSave.setCategory(category.get());
		bookToSave.setTitle(book.getTitle());

		bookRepository.save(bookToSave);

		return new ResponseEntity(bookToSave, HttpStatus.OK);
	}

	@GetMapping(value = "/books/{bookId}")
	public ResponseEntity findBook(@PathVariable("bookId") String bookId) {
		Optional<Book> bookToUpdate = bookRepository.findById(Integer.valueOf(bookId));
		if (!bookToUpdate.isPresent()) {
			return new ResponseEntity("book not present!", HttpStatus.BAD_REQUEST);
		}
		Book book = bookToUpdate.get();

		return new ResponseEntity(book, HttpStatus.OK);
	}
}
