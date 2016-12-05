package edu.cmpe275.team13.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class PatronController {
	
		@RequestMapping(value = "/greet", method=RequestMethod.GET)
		public ResponseEntity<String> greet(){
			return new ResponseEntity<String> ("Hi", HttpStatus.OK);
		}

}
