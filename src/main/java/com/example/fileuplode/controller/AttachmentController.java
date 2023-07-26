package com.example.fileuplode.controller;

import com.example.fileuplode.entity.Attachment;
import com.example.fileuplode.entity.AttachmentContent;
import com.example.fileuplode.repository.AttachmentContentRepository;
import com.example.fileuplode.repository.AttachmentRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;

@RestController
@RequestMapping
public class AttachmentController {
    @Autowired
    AttachmentContentRepository attachmentContentRepository;
    @Autowired
    AttachmentRepository attachmentRepository;


    @PostMapping("/uploadFile")
    public String upload(MultipartHttpServletRequest servletRequest) throws IOException {
        Iterator<String> fileNames = servletRequest.getFileNames();
        MultipartFile file = servletRequest.getFile(fileNames.next());
        if (file!=null) {
            String originalFilename = file.getOriginalFilename();
            long size = file.getSize();
            String contentType = file.getContentType();
            Attachment attachment = new Attachment();
            attachment.setOriginalName(originalFilename);
            attachment.setSize(size);
            attachment.setContentType(contentType);
            Attachment saveAttachment = attachmentRepository.save(attachment);
            AttachmentContent attachmentContent = new AttachmentContent();
            attachmentContent.setAttachment(attachment);
            attachmentContent.setContent(file.getBytes());
            attachmentContentRepository.save(attachmentContent);
            return "Save File ad:"+attachment.getId();
        }
        return "ERROR";
    }
    @GetMapping("/uploadFile/{id}")
    public void  getFile(@PathVariable Integer id, HttpServletResponse response) throws IOException {
        Optional<Attachment> optionalAttachment = attachmentRepository.findById(id);
        if (optionalAttachment.isPresent()){
            Attachment attachment = optionalAttachment.get();
            Optional<AttachmentContent> contentRepositoryById = attachmentContentRepository.findByAttachmentId(id);
            if (contentRepositoryById.isPresent()){
                AttachmentContent attachmentContent = contentRepositoryById.get();
                response.setHeader("Content-Disposition","attachment; fileName="+attachment.getOriginalName());
                response.setContentType(attachment.getContentType());
                FileCopyUtils.copy(attachmentContent.getContent(),response.getOutputStream());
            }
        }
    }
    @DeleteMapping("/uploadFile/{id}")
    public String delete(@PathVariable Integer id){
        Attachment attachment = attachmentRepository.findById(id).get();
        attachmentRepository.deleteById(id);
        attachmentContentRepository.deleteById(id);
        return "Delete File";

    }

}
