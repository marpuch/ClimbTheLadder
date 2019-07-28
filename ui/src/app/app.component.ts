import {Component, OnInit} from '@angular/core';
import {QueryService} from "./services/query.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'Climb The Ladder';
  name = "";
  public listItems;

  constructor(private queryService : QueryService) {}

  ngOnInit(): void {
    this.getListItems();
  }

  getListItems() {
    this.queryService.get().subscribe(
        data => { this.listItems = data },
        err => { console.error(err) },
        () => console.log("Items loaded")
    )
  }

  addLadder() {
    const data = { name : this.name, timestamp : Date.now() };
    console.log("Add ladder for name: " + data.name + " timestamp: " + data.timestamp);
    const body = JSON.stringify(data);
    this.queryService.add(body).subscribe(
      data => {
        this.listItems = data;
        this.name = "";
      },
      err => { console.error(err) },
      () => console.log("Ladder added")
    );

  }
}
