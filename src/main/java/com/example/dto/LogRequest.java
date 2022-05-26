package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@RedisHash("bvn")
@Data
@NoArgsConstructor
@With
@AllArgsConstructor
public class LogRequest implements Serializable {
    @Id
    String id;

    Bvn bvn;
    BvnResponse response;

    public LogRequest(Bvn bvn, BvnResponse response){
        //this.id = id;

        this.bvn = bvn;
        this.response = response;
    }

}
