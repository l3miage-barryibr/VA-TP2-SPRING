package fr.uga.l3miage.spring.tp2.exo1.repositories;

import fr.uga.l3miage.spring.tp2.exo1.models.ArtistEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import static fr.uga.l3miage.exo1.enums.GenreMusical.*;
import static org.assertj.core.api.Assertions.assertThat;


@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")
public class ArtistRepositoryTest {
    @Autowired
    private ArtistRepository artistRepository;

    @Test
    void testRequestCountByGenreMusicalEquals(){
        ArtistEntity artistEntity =  ArtistEntity
                .builder()
                .name("Nino")
                .genreMusical(RAP)
                .build();
        ArtistEntity artistEntity1 = ArtistEntity
                .builder()
                .name("Gazo")
                .genreMusical(RAP)
                .build();
        ArtistEntity artistEntity2 = ArtistEntity
                .builder()
                .genreMusical(CLASSIC)
                .name("Akon").build();
        artistRepository.save(artistEntity);
        artistRepository.save(artistEntity1);
        artistRepository.save(artistEntity2);

        int artistEntitiesResponse = artistRepository.countByGenreMusicalEquals(RAP);
        assertThat(artistEntitiesResponse).isEqualTo(2);




    }
}
