package com.sheryians.major.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javax.imageio.IIOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.sheryians.major.dto.ProductDTO;
import com.sheryians.major.model.Category;
import com.sheryians.major.model.Product;
//import com.sheryians.major.service.CategoryService;
import com.sheryians.major.service.CategoryService;
import com.sheryians.major.service.ProductService;


@Controller
public class AdminController {
	@Autowired
	CategoryService categoryService;
	
	
	
	
	@GetMapping("/admin")
	public String adminHome() {
		return "adminHome";
	}

	@GetMapping("/admin/categories")
	public String getCat(Model model) {
		model.addAttribute("categories", categoryService.getAllCategory());
		return "categories";
	}
	
	@GetMapping("/admin/categories/add")
	public String getCatAdd(Model model	) {
		model.addAttribute("category" ,new Category() );  // KEY DEFINED IN THE CategoriesAdd.html file 
		return "categoriesAdd";
	}
	
	
	@PostMapping("/admin/categories/add")
	public String postCatAdd(@ModelAttribute("category") Category category ) {
		categoryService.addCategory(category);
		return "redirect:/admin/categories";
	}	
	
	@GetMapping("/admin/categories/delete/{id}")
	public String deleteCat(@PathVariable int id) {
		categoryService.removeCategoryById(id);
		return "redirect:/admin/categories";
	}
	
	@GetMapping("/admin/categories/update/{id}")
	public String updateCat(@PathVariable int id, Model model) {
		Optional<Category> category = categoryService.getCategorybyId(id);
		if(category.isPresent()) {
			model.addAttribute("category" , category.get());
			return "categoriesAdd";
		}
		else 
			return "404";
	}
	
	
	
	
//	Product Section Is started From Here
	
	@Autowired
	ProductService productService;
	
	public static String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/productImages";
	
	
	
	@GetMapping("/admin/products")
	public String products(Model model) {
		model.addAttribute("products" , productService.getAllProduct());
		return "products";
	}

	@GetMapping("/admin/products/add")
	public String productAddGet(Model model) {
		model.addAttribute("productDTO" , new ProductDTO());
		
		model.addAttribute("categories" , categoryService.getAllCategory()); 
		//  Here the *categories* is defined in html page from where it reference to show the category we defined
		return "productsAdd";
	}
	
	//Method when we submit the product after filling all details of product for add (according to their category)
	@PostMapping("/admin/products/add")
	public String productAddPost(@ModelAttribute("productDTO") ProductDTO productDTO,
								 @RequestParam("productImage") MultipartFile file,
								 @RequestParam("imgName") String imgname)throws IOException{
		Product product = new Product();
		product.setId(productDTO.getId());
		product.setName(productDTO.getName());
		product.setCategory(categoryService.getCategorybyId( productDTO.getCategoryId() ).get());
		product.setPrice(productDTO.getPrice());
		product.setDescription(productDTO.getDescription());
		product.setWeight(productDTO.getWeight());
		
		String imageUUid;
		
		if(!file.isEmpty()) {
			imageUUid = file.getOriginalFilename();
			Path fileNameAndPath = Paths.get(uploadDir , imageUUid);
			Files.write(fileNameAndPath,file.getBytes());
		}else {
			imageUUid=imgname;
		}
		
		product.setImageName(imageUUid);
		productService.addProduct(product);
		
		
		return "redirect:/admin/products";
	}
	
	@GetMapping("/admin/product/delete/{id}")
	public String deleteProduct(@PathVariable Long id) {
		productService.removeProductById(id);
		return "redirect:/admin/products";
	}
	
	@GetMapping("/admin/product/update/{id}")
	public String updateProductGet(@PathVariable Long id , Model model ) {
		Product product = productService.getProductById(id).get();
		
		ProductDTO productDTO = new ProductDTO();
		productDTO.setId(product.getId());
		productDTO.setName(product.getName());
		productDTO.setCategoryId(product.getCategory().getId());
		productDTO.setWeight(product.getWeight());
		productDTO.setWeight(product.getWeight());
		productDTO.setDescription(product.getDescription());
		productDTO.setImageName(product.getImageName());
		
		model.addAttribute("categories" , categoryService.getAllCategory());
		model.addAttribute("productDTO" , productDTO);
		
	
	
	
		return "productsAdd";
	}
	
	
	
	
}
