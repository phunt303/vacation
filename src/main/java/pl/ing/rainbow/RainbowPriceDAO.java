package pl.ing.rainbow;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Hunt on 2017-07-24.
 */
public interface RainbowPriceDAO extends CrudRepository<RainbowPrice, Long> {

    @Query(value = "SELECT period, MIN(price) as price FROM RAINBOW_PRICE where (period like '%.08%' or period like '%.09%') and period not like '%(15 dni)%' group by period order by period", nativeQuery = true)
    List<Object[]> findBest();
}
