package cz.muni.fi.pa165.travelagency.springmvc.controllers;

import cz.muni.fi.pa165.travelagency.dto.ExcursionDTO;
import cz.muni.fi.pa165.travelagency.dto.ReservationDTO;
import cz.muni.fi.pa165.travelagency.dto.TripCreateDTO;
import cz.muni.fi.pa165.travelagency.dto.TripDTO;
import cz.muni.fi.pa165.travelagency.dto.UserDTO;
import cz.muni.fi.pa165.travelagency.facade.ExcursionFacade;
import cz.muni.fi.pa165.travelagency.facade.ReservationFacade;
import cz.muni.fi.pa165.travelagency.facade.TripFacade;
import cz.muni.fi.pa165.travelagency.facade.UserFacade;
import cz.muni.fi.pa165.travelagency.sampledata.SampleDataLoadingFacadeImpl;
import cz.muni.fi.pa165.travelagency.springmvc.forms.TripCreateDTOValidator;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;


/**
 * @author Ondrej Glasnak
 */
@Controller
@RequestMapping("/shopping/trip")
public class TripController {

    final static Logger log = LoggerFactory.getLogger(SampleDataLoadingFacadeImpl.class);

    @Autowired
    private TripFacade tripFacade;

    @Autowired
    private UserFacade userFacade;
    
    @Autowired
    private ExcursionFacade excursionFacade;
    
    @Autowired
    private ReservationFacade reservationFacade;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String listTrips(Model model, HttpServletRequest req) {
        log.error("request: GET /shopping/trip/list");
        HttpSession session = req.getSession(true);
        UserDTO user = (UserDTO) session.getAttribute("authUser");
        if (userFacade.isUserAdmin(user.getId())) {
            model.addAttribute("trips", tripFacade.getAllTrips());
        } else {
            model.addAttribute("trips", tripFacade.getTripsByUser(user.getId()));
        }
        return "/shopping/trip/list";
    }

    @RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
    public String showTrip(
            @PathVariable("id") long id,
            Model model) {

        log.error("request: GET /shopping/trip/view/" + id);
        TripDTO trip = tripFacade.getTripById(id);
        if (trip == null) {
            return "redirect:/shopping";
        }
        model.addAttribute("trip", trip);
        return "/shopping/trip/view";
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public String delete(@PathVariable long id, 
            Model model,
            UriComponentsBuilder uriBuilder,
            RedirectAttributes redirectAttributes) {
        
        TripDTO trip = tripFacade.getTripById(id);
        
        tripFacade.deleteTrip(id);
        log.debug("delete({})", id);
        
        redirectAttributes.addFlashAttribute("alert_success", "Trip \"" + trip.getName() + "\" was deleted.");
        return "redirect:" + uriBuilder.path("/shopping/trip/list").toUriString();
    }
    
    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String newTrip(Model model){
        log.error("new()");
        model.addAttribute("tripCreate", new TripCreateDTO());
        return "shopping/trip/new";
    }
    
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        CustomDateEditor editor = new CustomDateEditor(dateFormat, true);
        binder.registerCustomEditor(Date.class, editor);
        if (binder.getTarget() instanceof TripCreateDTO) {
            TripCreateDTOValidator validator = new TripCreateDTOValidator();
            binder.addValidators(validator);
        }
    }
    
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createTrip(@Valid @ModelAttribute("tripCreate") TripCreateDTO formBean,
            BindingResult bindingResult,
            Model model, 
            RedirectAttributes redirectAttributes, 
            UriComponentsBuilder uriBuilder) {
        
        log.error("create(tripCreate={})", formBean);
        
        if (bindingResult.hasErrors()) {
            for (ObjectError ge : bindingResult.getGlobalErrors()) {
                log.error("ObjectError: {}", ge);
            }
            for (FieldError fe : bindingResult.getFieldErrors()) {
                model.addAttribute(fe.getField() + "_error", true);
                log.error("FieldError: {}", fe);
            }
            return "/shopping/trip/new";
        }
        
        Long id = tripFacade.createTrip(formBean);
        
        redirectAttributes.addFlashAttribute("alert_success", "Trip with " + id + " was created");
        return "redirect:" + uriBuilder.path("/shopping/trip/view/{id}").buildAndExpand(id).encode().toUriString();
    }
}