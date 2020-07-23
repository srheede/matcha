package wethinkcode.co.za.matcha;

public class User {

    private String  platform;
    private String  username;
    private String  firstName;
    private String  lastName;
    private String  email;
    private String  gender;
    private String  birthDate;
    private String  sexPref;
    private String  bio;
    private String  interests;
    private String  profPic;
    private String  pic2;
    private String  pic3;
    private String  pic4;
    private String  pic5;
    private String  location;
    private String  notifications;
    private String  sortBy;
    private String  filterDistance;
    private String  filterAgeMax;
    private String  filterAgeMin;
    private String  filterInterests;
    private String  filterLocation;
    private String  popularity;

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getFilterDistance() {
        return filterDistance;
    }

    public void setFilterDistance(String filterDistance) {
        this.filterDistance = filterDistance;
    }

    public String getFilterInterests() {
        return filterInterests;
    }

    public void setFilterInterests(String filterInterests) {
        this.filterInterests = filterInterests;
    }

    public String getFilterLocation() {
        return filterLocation;
    }

    public void setFilterLocation(String filterLocation) {
        this.filterLocation = filterLocation;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public void setGeoHash(String geoHash) {
        this.geoHash = geoHash;
    }

    private String  geoHash;

    public User(){
        platform = "matcha";
        notifications = "yes";
    }

    public String getPic2() {
        return pic2;
    }

    public void setPic2(String pic2) {
        this.pic2 = pic2;
    }

    public String getPic3() {
        return pic3;
    }

    public void setPic3(String pic3) {
        this.pic3 = pic3;
    }

    public String getPic4() {
        return pic4;
    }

    public void setPic4(String pic4) {
        this.pic4 = pic4;
    }

    public String getPic5() {
        return pic5;
    }

    public void setPic5(String pic5) {
        this.pic5 = pic5;
    }

    public String getNotifications() {
        return notifications;
    }

    public void setNotifications(String notifications) {
        this.notifications = notifications;
    }

    public String getPlatform() {
        return platform;
    }

    public String getInterests() {
        return interests;
    }

    public String getProfPic() {
        return profPic;
    }

    public void setProfPic(String profPic) {
        this.profPic = profPic;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSexPref() {
        return sexPref;
    }

    public void setSexPref(String sexPref) {
        this.sexPref = sexPref;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFilterAgeMax() {
        return filterAgeMax;
    }

    public void setFilterAgeMax(String filterAgeMax) {
        this.filterAgeMax = filterAgeMax;
    }

    public String getFilterAgeMin() {
        return filterAgeMin;
    }

    public void setFilterAgeMin(String filterAgeMin) {
        this.filterAgeMin = filterAgeMin;
    }
}