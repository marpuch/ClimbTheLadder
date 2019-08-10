import {Component, OnInit} from '@angular/core';
import {QueryService} from "./services/query.service";
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'Climb The Ladder';
  name = "";
  public listItems;
  handler = () => {
      console.log("Automated refresh");
      this.getListItems();
      this.registerCallAgain();
  };
  interval = -1;

  constructor(private toastr: ToastrService, private queryService : QueryService) {}

  ngOnInit(): void {
    this.toastr.toastrConfig.positionClass = 'toast-bottom-right';
    this.getListItems();
    // this.listItems = { shortList : [
    //         {"position" : 1, "name" : "Ala ma kota", "timestamp" : Date.now(), "ladderCount" : 1, "level" : 1},
    //         {"position" : 2, "name" : "Basia", "timestamp" : Date.now() - 5000000, "ladderCount" : 1, "level" : 1},
    //     ],
    //     highlightIndex : 0}
      this.registerCallAgain();
  }

  registerCallAgain() {
      if (this.interval != -1) clearInterval(this.interval);
      this.interval = setInterval(this.handler, 60000);
  }

  getListItems() {
    this.queryService.get().subscribe(
        data => { this.listItems = data; this.registerCallAgain()},
        err => { console.error(err) },
        () => console.log("Items loaded")
    )
  }

  addLadder() {
    const data = { name : this.name, timestamp : Date.now(), level2Ladders : this.listItems.level2LadderPayload };
    console.log("Add ladder for name: " + data.name
        + " timestamp: " + data.timestamp
        + " payload: " + data.level2Ladders);
    const body = JSON.stringify(data);
    this.queryService.add(body).subscribe(
      data => {
        this.listItems = data;
        this.showToast(true, "Ladder added successfully");
        this.name = "";
        this.registerCallAgain();
      },
      err => {
        console.error(err);
        if (err.status == 400) {
            this.listItems = err.error;
            this.showToast(false, "Http status 400: " + this.listItems.errorMessage);
            this.registerCallAgain();
        } else if (err.status == 429) {
            this.showToast(false, "Http status 429: ♪♫♬ Slow down, you're movin' to fast ♪♫♬")
        } else {
            this.showToast(false, err.message);
        }
      },
      () => console.log("Ladder added")
    )
  }

  showToast(ok : boolean, message : string){
    if (ok) {
      this.toastr.success(message);
    } else {
      this.toastr.warning(message);
    }
  }

  renderTime(timestamp : number, level : string) {
      if (level != "LEVEL1") {
          return "-";
      }

      const now = Date.now();
      const hour_in_milisec = 3600000;
      const next_add_possible = timestamp + hour_in_milisec;
      if (next_add_possible < now) {
          return "Now!"
      }
      const diff_sec = Math.trunc((next_add_possible - now)/1000);
      if (diff_sec < 60) {
          return diff_sec + " seconds";
      } else {
          return Math.trunc(diff_sec/60) + " minutes";
      }
  }
}
