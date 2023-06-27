package com.haemil.backend.alert.entity;

import com.haemil.backend.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Slf4j
@Getter
@Entity
@NoArgsConstructor
@Table(name = "AlertApi")
public class AlertApi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private Long id;

    @Column(nullable = false)
    private String msg;

    @Column(nullable = false)
    private String location;

    @Builder
    public AlertApi(String msg, String location) {
        this.msg = msg;
        this.location = location;
    }

}
