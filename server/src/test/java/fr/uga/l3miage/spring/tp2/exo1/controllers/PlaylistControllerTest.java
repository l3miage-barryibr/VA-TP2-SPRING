package fr.uga.l3miage.spring.tp2.exo1.controllers;

import fr.uga.l3miage.exo1.errors.NotFoundErrorResponse;
import fr.uga.l3miage.exo1.requests.PlaylistCreationRequest;
import fr.uga.l3miage.exo1.response.PlaylistResponseDTO;
import fr.uga.l3miage.spring.tp2.exo1.components.PlaylistComponent;
import fr.uga.l3miage.spring.tp2.exo1.components.SongComponent;
import fr.uga.l3miage.spring.tp2.exo1.models.PlaylistEntity;
import fr.uga.l3miage.spring.tp2.exo1.repositories.PlaylistRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@AutoConfigureTestDatabase
@AutoConfigureWebClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")
public class PlaylistControllerTest {
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private PlaylistRepository playlistRepository;
    @SpyBean
    private PlaylistComponent playlistComponent;
    @SpyBean
    private SongComponent songComponent;
    @AfterEach
    public void clear(){
        this.playlistRepository.deleteAll();
    }
    @Test

    void canCreatePlaylistWithoutSong(){
        //given

        final HttpHeaders headers = new HttpHeaders();

        final PlaylistCreationRequest request = PlaylistCreationRequest
                .builder()
                .name("Test playlist")
                .description("une playlist de test")
                .songsIds(Set.of())
                .build();
        //when

        ResponseEntity<PlaylistResponseDTO> response = testRestTemplate.exchange("/api/playlist/create", HttpMethod.POST, new HttpEntity<>(request, headers), PlaylistResponseDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(playlistRepository.count()).isEqualTo(1);
        verify(playlistComponent, times(1)).createPlaylistEntity(any(PlaylistEntity.class));
        verify(songComponent, times(1)).getSetSongEntity(Set.of());
    }
    @Test
    void getNotFoundPlaylist() {
        //Given
        final HttpHeaders headers = new HttpHeaders();

        final Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("idPlaylist", "ma playlist qui n'existe pas");

        NotFoundErrorResponse notFoundErrorResponseExpected = NotFoundErrorResponse
                .builder()
                .uri("/api/playlist/ma%20playlist%20qui%20n%27existe%20pas")
                .errorMessage("La playlist [ma playlist qui n'existe pas] n'a pas été trouvé")
                .build();

        //when
        ResponseEntity<NotFoundErrorResponse> response = testRestTemplate.exchange("/api/playlist/{idPlaylist}", HttpMethod.GET, new HttpEntity<>(null, headers), NotFoundErrorResponse.class, urlParams);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).usingRecursiveComparison()
                .isEqualTo(notFoundErrorResponseExpected);
    }
}
