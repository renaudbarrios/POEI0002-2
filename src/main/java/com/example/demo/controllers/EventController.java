package com.example.demo.controllers;

import com.example.demo.entities.Event;
import com.example.demo.entities.User;
import com.example.demo.entities.UserEventOrganisator;
import com.example.demo.entities.UserEventParticipant;
import com.example.demo.entities.dtos.EventDto;
import com.example.demo.exeptions.NotFoundException;

import com.example.demo.services.Mapper;
import com.example.demo.services.eventservices.EventCrudService;
import com.example.demo.services.usereventorganisatorservices.UserEventOrganisatorService;
import com.example.demo.services.usereventparticipantservice.UserEventParticipantService;
import com.example.demo.services.userservices.UserCrudService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;



/**
 * Controller of the event entity.
 * @author Cedrick Pennec.
 */
@RestController
@RequestMapping("api/v1/event")
@Api(value = "Event Management System", tags = "Events")
public class EventController {


  /**
 * CRUD service of an Event.
 */
  @Autowired
  private EventCrudService service;

  /**
 * CRUD service of a User.
 */
  @Autowired
  private UserCrudService userService;

  /**
 * CRUD service of a UserEventParticipant.
 */
  @Autowired
  private UserEventParticipantService userEventParticipantService;

  /**
 * CRUD service of a UserEventOrganisator.
 */
  @Autowired
  private UserEventOrganisatorService userEventOrganisatorService;

  /**
 * Mapper used to convert entities to their DTO.
 */
  @Autowired
  private Mapper mapperDto;

  /**
 * @param pageable containing a page number and a number of elements.
 * @return Return a page of users.
 */
  @GetMapping("/public")
  @ApiOperation(value = "Retrieves all events")
  public Page<EventDto> getAll(final Pageable pageable) {
    Page<Event> eventPage = this.service.getAll(pageable);
    eventPage.getTotalElements();
    return new PageImpl<>(eventPage.stream()
        .map(event -> mapperDto.eventToDto(event))
        .collect(Collectors.toList()), pageable, eventPage.getTotalElements());
  }

  /**
 * @param id of an event.
 * @return Return a list of users participating to the event with the defined id.
 * @throws NotFoundException Event was not found.
 */
  @GetMapping("/public/participants/{id}")
  @ApiOperation(value = "Retrieves all users participating to the event")
  public List<User> getAllParticipants(@PathVariable final Long id) throws NotFoundException {
    Event event = this.service.getOne(id);
    List<UserEventParticipant> evepart = event.getParticipants();
    List<User> result = new ArrayList<>();
    for (UserEventParticipant u : evepart) {
      result.add(u.getUserParticipant());
    }
    return result;
  }

  /**
   * @param id of an event
   * @return Return a list of users organizing the event with the defined id.
   * @throws NotFoundException Event was not found.
   */
  @GetMapping("/public/organisators/{id}")
  @ApiOperation(value = "Retrieves all users organisators to the event")
  public List<User> getAllOrganisators(@PathVariable final Long id) throws NotFoundException {
    Event event = this.service.getOne(id);
    List<UserEventOrganisator> eventOrganisators = event.getOrganisators();
    List<User> result = new ArrayList<>();
    for (UserEventOrganisator u : eventOrganisators) {
      result.add(u.getUserOrganisator());
    }
    return result;
  }

  /**
   * Set a user defined by his username as organizer of an event defined by its id.
   * @param id of an event.
   * @throws NotFoundException User or Event was not found.
   */
  @GetMapping("/organisator/{id}")
  @ApiOperation(value = "Add a user as organizer of an event")
  public void addOrganisator(@PathVariable final Long id)
                             throws NotFoundException {
    Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
    String username = loggedInUser.getName();
    User user = this.userService.getByUserName(username);
    Event event = this.service.getOne(id);
    UserEventOrganisator userEventOrganisator = new UserEventOrganisator();
    userEventOrganisator.setEventOrganisator(event);
    userEventOrganisator.setUserOrganisator(user);

    this.userEventOrganisatorService.save(userEventOrganisator);
  }

  /**
 * @param eventDto DTO of an event.
 * @return Return the event created.
 * @throws NotFoundException User was not found.
 */
  @PostMapping
  @ApiOperation(value = "Create an event")
  @ResponseStatus(HttpStatus.CREATED)
  public Event create(@Valid @RequestBody final EventDto eventDto) throws NotFoundException {
    Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
    Event event = new Event();
    String username = loggedInUser.getName();
    User user = this.userService.getByUserName(username);
    event.setAuthor(user);
    event = this.mapperDto.dtoToEvent(event, eventDto);
    return this.service.create(event);
  }

  /**
 * @param id id of an event
 * @param eventDto DTO of an event
 * @return return the event updated
 * @throws NotFoundException Event was not found.
 */
  @PutMapping("{id}")
  @ApiOperation(value = "Update an event")
  public Event update(@PathVariable final Long id, @Valid @RequestBody final EventDto eventDto)
      throws NotFoundException {

    final Event event = this.service.getOne(id);
    this.service.update(this.mapperDto.dtoToEvent(event, eventDto));
    return event;
  }

  /**
   * Add a user determined by an authentication token, to an event determined by its id.
   * @param id Id of an Event
   * @throws NotFoundException Event or User was not found.
   */
  @GetMapping("/join/{id}")
  @ApiOperation(value = "Add a user to an event as participant")
  public void join(@PathVariable final Long id) throws NotFoundException {
    Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
    String username = loggedInUser.getName();
    User user = this.userService.getByUserName(username);
    Event event = this.service.getOne(id);

    if(event.getNbFree() > 0) {
        UserEventParticipant usereventpart = new UserEventParticipant();
        usereventpart.setUserParticipant(user);
        usereventpart.setEventParticipant(event);

        this.userEventParticipantService.save(usereventpart);

        user.addAsParticipant(usereventpart);
        this.userService.update(user);

        event.addParticipant(usereventpart);
        this.service.update(event);
    }
  }

  @DeleteMapping("disjoin/{id}")
  public void disjoin(@PathVariable final Long id) throws NotFoundException {
    Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
    String username = loggedInUser.getName();
    User user = this.userService.getByUserName(username);
    Event event = this.service.getOne(id);
    UserEventParticipant userEventParticipant = this.userEventParticipantService
            .getOne(user, event);
    this.userEventParticipantService.delete(userEventParticipant);
    event.removeParticipant(userEventParticipant);
    this.service.update(event);
  }


  /**
   * Delete an event.
   * @param id Id of an Event.
   */
  @DeleteMapping("{id}")
  @ApiOperation(value = "Delete an event")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable final Long id) {
    this.service.delete(id);
  }

  /**
 * @param id of an Event
 * @return Return an event
 * @throws NotFoundException Event was not found.
 */
  @GetMapping("/public/{id}")
  @ApiOperation(value = "Retrieve an event")
  public EventDto getOne(@PathVariable final Long id) throws NotFoundException {
    return mapperDto.eventToDto(this.service.getOne(id));
  }
}
