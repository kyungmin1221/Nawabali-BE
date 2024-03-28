package com.nawabali.nawabali.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@NoArgsConstructor
@Table (name = "comment")
@Slf4j(topic = "CommentDomain 로그")
public class Comment {
}
