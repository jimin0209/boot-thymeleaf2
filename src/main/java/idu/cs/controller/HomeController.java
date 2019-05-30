package idu.cs.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import idu.cs.domain.User;
import idu.cs.exception.ResourceNotFoundException;
import idu.cs.repository.UserRepository;

@Controller
// 애노테이션 : 컴파일러에게 설정 내용이나 상태를 알려주는 목적, 적용범위가 클래스 내부로 한정
public class HomeController {
   @Autowired UserRepository userRepo; //Dependency Injection
   
   @GetMapping("/")
   public String loadWelcome(Model model){
      return "index";
   }
   
   @GetMapping("/login-form")
   public String loginForm(Model model){
      return "login";
   }   
   @GetMapping("/users")
   public String getAllUser(Model model) {
      model.addAttribute("users", userRepo.findAll());
      return "userlist";
   }
   @GetMapping("/users/byname") // byname?name=***, *** 값이 name 변수에 저장
   public String getUsersByName(@Param(value = "name") String name, Model model) {
      List<User> users = userRepo.findByName(name);
      model.addAttribute("users", users);
      
      return "userlist";
   }
   @GetMapping("/users/nameasc") // byname?name=***, *** 값이 name 변수에 저장
   public String getUsersByNameAsc(@Param(value = "name") String name, Model model) {
      List<User> users = userRepo.findByNameOrderByIdAsc(name);
      model.addAttribute("users", users);
      
      return "userlist";
   }
   @PostMapping("/login")
   // 실제 로그인 처리, user : 입력한 내용에 대한 객체, sessionUser : Repository로부터 가져온 내용의 객체
   public String userLogin(@Valid User user, HttpSession session){
      System.out.println("login process : ");
      User sessionUser = userRepo.findByUserId(user.getUserId());
      if(sessionUser == null) {
         System.out.println("id error : ");
         return "redirect:/login-form";
      }
      if(!sessionUser.getUserPw().equals(user.getUserPw())) {
         System.out.println("pw error : ");
         return "redirect:/login-form";
      }
      session.setAttribute("user", sessionUser);
      return "redirect:/";
   }   
   @GetMapping("/logout")
   public String logoutUser(HttpSession session){
	   //session.invalidate();
	   session.removeAttribute("user");
      return "redirect:/";
   }
   @GetMapping("/users/{id}")
   public String getUserById(@PathVariable(value = "id") Long userId, 
         Model model ) throws ResourceNotFoundException {
      //User user = userRepo.findById(userId).get();
            User user = userRepo.findById(userId)
                  .orElseThrow(() -> 
                  new ResourceNotFoundException("not found " + userId ));
      model.addAttribute("user", user);
      return "info";
   }   
   @GetMapping("/register-form")
   public String regform(Model model) {      
      return "register";
   }
   @PutMapping("/users/{id}")
   //@RequestMaping(value=""/users/{id}" method=RequestMethod.UPDATE)
   public String updateUserById(@PathVariable(value = "id") Long userId, 
         @Valid User userDetails, Model model ) throws ResourceNotFoundException {
         // userDatials 폼을 통해 전송된 객체, user는 id로 jpa를 통해서 가져온 객체
      User user = userRepo.findById(userId)
                  .orElseThrow(() -> 
                  new ResourceNotFoundException("not found " + userId ));
      user.setName(userDetails.getName());
      user.setCompany(userDetails.getCompany());
      
      User userUpdate = userRepo.save(user);
      // 해당되는 user 의 아이디를 가지고 객체를 삭제 -> jpa : record 삭제로 적용
      // model.addAttribute("name", user.getName());
      
      return "redirect:/users";
      // 업데이트가 성공하면 users 자원을  get 방식으로 접근 하되 model에 user 어트리뷰트를 전달하면서 리 다이렉션
      //return ResponseEntity.ok(userUpdate);
   }   
   @PostMapping("/users")
   public String createUser(@Valid User user, Model model) {
      userRepo.save(user);
      model.addAttribute("user", userRepo.findAll());
      return "redirect:/users";
   }   
   @DeleteMapping("/users/{id}")
   //@RequestMaping(value=""/users/{id}" method=RequestMethod.DELETE)
   public String deleteUserById(@PathVariable(value = "id") Long userId, 
         Model model ) throws ResourceNotFoundException {
            User user = userRepo.findById(userId)
                  .orElseThrow(() -> 
                  new ResourceNotFoundException("not found " + userId ));
      userRepo.delete(user);
      // 해당되는 user 의 아이디를 가지고 객체를 삭제 -> jpa : record 삭제로 적용
      model.addAttribute("name", user.getName());
      return "disjoin";
   }  
}