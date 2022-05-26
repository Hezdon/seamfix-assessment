package com.example.controller;

import com.example.dto.Bvn;
import com.example.dto.BvnResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import static com.example.util.Const.*;

@RestController
public class DispatchController {


    @ResponseBody
    @PostMapping(value = "/bv-service/svalidate/wrapper", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> bvnValidation(@RequestBody @Valid Bvn request){

        BvnResponse response = null;

        if(request.getBvn().startsWith("33333")){
            response = new BvnResponse().withCode(BVN_NOT_FOUND_CODE)
                    .withBvn(request.getBvn())
                    .withMessage("The searched BVN does not exist");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if(request.getBvn().matches("[0-9]{11}")) {
            response = new BvnResponse().withCode(BVN_SUCCESS_CODE)
                    .withBvn(request.getBvn())
                    .withMessage(SUCCESS)
                    .withImageDetail("data:image/png;base64,098yuh8he8uyg7gssyyg")
                    .withBasicDetail("data:image/png;base64,098yuh8he8uyg7gssyyg");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        if(request.getBvn().isBlank())
            response = new BvnResponse().withCode(BVN_BAD_REQUEST)
                    .withBvn(request.getBvn())
                    .withMessage("One or more of your request parameters failed validation. Please retry");

        else if(request.getBvn().length() < 11)
            response = new BvnResponse().withCode(BVN_INVALID_CODE)
                    .withBvn(request.getBvn())
                    .withMessage("The searched BVN is invalid");

        else if(!request.getBvn().matches("[0-9]+"))
            response = new BvnResponse().withCode(BVN_BAD_REQUEST)
                    .withBvn(request.getBvn())
                    .withMessage("The searched BVN is invalid");

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }




}
