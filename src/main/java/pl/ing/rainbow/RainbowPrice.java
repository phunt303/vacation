package pl.ing.rainbow;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "rainbowPrice")
public class RainbowPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="readTime")
    private Date requestTime;

    @Column(name="period")
    private String period;

    @Column(name="price")
    private Integer price;

    public RainbowPrice() {
    }

    public RainbowPrice(String period, Integer price) {
        this.requestTime = Date.from(Instant.now());
        this.period = period;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}