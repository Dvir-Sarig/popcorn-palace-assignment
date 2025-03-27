package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.dto.ShowtimeDTO;
import com.att.tdp.popcorn_palace.exceptions.GlobalExceptionHandler;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.service.ShowtimeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.time.LocalDateTime;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ShowtimeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ShowtimeService showtimeService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        ShowtimeController controller = new ShowtimeController(showtimeService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createShowtime_WithValidData_ShouldReturnCreatedShowtime() throws Exception {
        ShowtimeDTO showtimeDTO = createShowtimeDTO();
        Showtime showtime = createShowtime(1L, showtimeDTO);
        when(showtimeService.createShowtime(any(ShowtimeDTO.class))).thenReturn(showtime);

        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtimeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(showtime.getId()));
    }

    @Test
    void createShowtime_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        ShowtimeDTO showtimeDTO = new ShowtimeDTO();

        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtimeDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getShowtimeById_WhenExists_ShouldReturnShowtime() throws Exception {
        Long showtimeId = 1L;
        Showtime showtime = createShowtime(showtimeId, createShowtimeDTO());
        when(showtimeService.getShowtimeById(showtimeId)).thenReturn(showtime);

        mockMvc.perform(get("/showtimes/{showtimeId}", showtimeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(showtime.getId()));
    }

    @Test
    void getShowtimeById_WhenNotFound_ShouldReturnNotFound() throws Exception {
        Long showtimeId = 999L;
        when(showtimeService.getShowtimeById(showtimeId)).thenThrow(new RuntimeException("Showtime not found"));

        mockMvc.perform(get("/showtimes/{showtimeId}", showtimeId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Showtime not found"));
    }

    @Test
    void updateShowtime_WithValidData_ShouldReturnUpdatedShowtime() throws Exception {
        Long showtimeId = 1L;
        ShowtimeDTO showtimeDTO = createShowtimeDTO();
        Showtime showtime = createShowtime(showtimeId, showtimeDTO);
        when(showtimeService.updateShowtime(eq(showtimeId), any(ShowtimeDTO.class))).thenReturn(showtime);

        mockMvc.perform(post("/showtimes/update/{showtimeId}", showtimeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtimeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(showtime.getId()));
    }

    @Test
    void deleteShowtime_WhenExists_ShouldReturnSuccessMessage() throws Exception {
        Long showtimeId = 1L;
        doNothing().when(showtimeService).deleteShowtime(showtimeId);

        mockMvc.perform(delete("/showtimes/{showtimeId}", showtimeId))
                .andExpect(status().isOk())
                .andExpect(content().string("Showtime deleted successfully."));

        verify(showtimeService, times(1)).deleteShowtime(showtimeId);
    }

    @Test
    void deleteShowtime_WhenNotFound_ShouldReturnNotFound() throws Exception {
        Long showtimeId = 999L;
        doThrow(new RuntimeException("Showtime not found")).when(showtimeService).deleteShowtime(showtimeId);

        mockMvc.perform(delete("/showtimes/{showtimeId}", showtimeId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Showtime not found"));
    }

    private ShowtimeDTO createShowtimeDTO() {
        ShowtimeDTO dto = new ShowtimeDTO();
        dto.setMovieId(1L);
        dto.setTheater("Theater 1");
        dto.setPrice(12.99);
        dto.setStartTime(LocalDateTime.now().plusDays(1));
        dto.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
        return dto;
    }

    private Showtime createShowtime(Long id, ShowtimeDTO dto) {
        Showtime showtime = new Showtime(dto);
        showtime.setId(id);
        return showtime;
    }
}
