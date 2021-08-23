package com.tecforte.blog.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tecforte.blog.service.BlogService;
import com.tecforte.blog.service.EntryService;
import com.tecforte.blog.service.dto.BlogDTO;
import com.tecforte.blog.service.dto.EntryDTO;
import com.tecforte.blog.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.tecforte.blog.domain.Blog}.
 */
@RestController
@RequestMapping("/api")
public class BlogResource {

    private final Logger log = LoggerFactory.getLogger(BlogResource.class);

    private static final String ENTITY_NAME = "blog";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BlogService blogService;

    private final EntryService entryService;
                        
    public BlogResource(BlogService blogService, EntryService entryService) {
        this.blogService = blogService;
        this.entryService = entryService;
    }

    /**
     * {@code POST  /blogs} : Create a new blog.
     *
     * @param blogDTO the blogDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new blogDTO, or with status {@code 400 (Bad Request)} if the blog has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/blogs")
    public ResponseEntity<BlogDTO> createBlog(@Valid @RequestBody BlogDTO blogDTO) throws URISyntaxException {
        log.debug("REST request to save Blog : {}", blogDTO);
        if (blogDTO.getId() != null) {
            throw new BadRequestAlertException("A new blog cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BlogDTO result = blogService.save(blogDTO);
        return ResponseEntity.created(new URI("/api/blogs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /blogs} : Updates an existing blog.
     *
     * @param blogDTO the blogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated blogDTO,
     * or with status {@code 400 (Bad Request)} if the blogDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the blogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/blogs")
    public ResponseEntity<BlogDTO> updateBlog(@Valid @RequestBody BlogDTO blogDTO) throws URISyntaxException {
        log.debug("REST request to update Blog : {}", blogDTO);
        if (blogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        BlogDTO result = blogService.save(blogDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, blogDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /blogs} : get all the blogs.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of blogs in body.
     */
    @GetMapping("/blogs")
    public List<BlogDTO> getAllBlogs() {
        log.debug("REST request to get all Blogs");
        return blogService.findAll();
    }

    /**
     * {@code GET  /blogs/:id} : get the "id" blog.
     *
     * @param id the id of the blogDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the blogDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/blogs/{id}")
    public ResponseEntity<BlogDTO> getBlog(@PathVariable Long id) {
        log.debug("REST request to get Blog : {}", id);
        Optional<BlogDTO> blogDTO = blogService.findOne(id);
        return ResponseUtil.wrapOrNotFound(blogDTO);
    }

    /**
     * {@code DELETE  /blogs/:id} : delete the "id" blog.
     *
     * @param id the id of the blogDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/blogs/{id}")
    public ResponseEntity<Void> deleteBlog(@PathVariable Long id) {
        log.debug("REST request to delete Blog : {}", id);
        blogService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
    
    @DeleteMapping("/blogs/clean")
    public ResponseEntity<Void> cleanBlog() {
        log.debug("REST request to clean Blogs");
        List<BlogDTO> blogDTO =  getAllBlogs();
        for(int i = 0; i < blogDTO.size(); i++) {
            List<EntryDTO> entryDTO =  entryService.findAllEntriesByBlogId(blogDTO.get(i).getId());
        	for(int j = 0; j < entryDTO.size(); j++) {
        		if(entryDTO.get(j).getContent().contains("rubbish") || entryDTO.get(j).getContent().contains("hell") ||
                	entryDTO.get(j).getTitle().contains("rubbish") || entryDTO.get(j).getTitle().contains("hell")){
                	entryService.delete(entryDTO.get(j).getId());
                }
        	}
        }
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, "")).build();
    }
    
    @DeleteMapping("/blogs/{id}/clean")
    public ResponseEntity<Void> cleanBlogById(@PathVariable Long id) {
        log.debug("REST request to clean Blog : {}", id);
        List<EntryDTO> entryDTO =  entryService.findAllEntriesByBlogId(id);
    	for(int i = 0; i < entryDTO.size(); i++) {
    		if(entryDTO.get(i).getContent().contains("rude") || entryDTO.get(i).getContent().contains("lol") || entryDTO.get(i).getContent().contains("rofl") ||
            	entryDTO.get(i).getTitle().contains("rude") || entryDTO.get(i).getTitle().contains("lol") || entryDTO.get(i).getTitle().contains("rofl")){
            	entryService.delete(entryDTO.get(i).getId());
            }
    	}
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
