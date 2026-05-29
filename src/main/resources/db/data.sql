CREATE TABLE IF NOT EXISTS expert_profile (
    id            BIGINT         NOT NULL AUTO_INCREMENT,
    member_id     BIGINT         NOT NULL,
    profile_image VARCHAR(255)   NOT NULL,
    intro         TEXT,
    career        TEXT,
    speciality    VARCHAR(255),
    avg_rating    DECIMAL(3,2)   NOT NULL DEFAULT 0.00,
    total_reviews INT            NOT NULL DEFAULT 0,
    is_certified  BOOLEAN        NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY uq_expert_member (member_id),
    CONSTRAINT fk_expert_member FOREIGN KEY (member_id) REFERENCES member (id)
);


CREATE TABLE IF NOT EXISTS payment (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    project_id   BIGINT       NOT NULL,
    client_id    BIGINT       NOT NULL,
    expert_id    BIGINT       NOT NULL,
    amount       INT          NOT NULL COMMENT '총 결제 금액',
    platform_fee INT          NOT NULL COMMENT '플랫폼 수수료 (10%)',
    net_amount   INT          NOT NULL COMMENT '전문가 수령액 (amount - platform_fee)',
    payment_key  VARCHAR(255)          COMMENT '포트원 imp_uid',
    method       VARCHAR(50)           COMMENT '결제 수단 (card, kakaopay 등)',
    status       ENUM('PAID','SETTLED','REFUNDED') NOT NULL DEFAULT 'PAID',
    paid_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id),
    UNIQUE KEY uq_payment_project (project_id),
    CONSTRAINT fk_payment_project FOREIGN KEY (project_id) REFERENCES project (id),
    CONSTRAINT fk_payment_client  FOREIGN KEY (client_id)  REFERENCES member  (id),
    CONSTRAINT fk_payment_expert  FOREIGN KEY (expert_id)  REFERENCES member  (id)
);

