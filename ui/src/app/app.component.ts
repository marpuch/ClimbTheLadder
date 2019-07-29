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

  constructor(private toastr: ToastrService, private queryService : QueryService) {}

  ngOnInit(): void {
    this.toastr.toastrConfig.positionClass = 'toast-bottom-right';
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
        this.showToaster(true, "Ladder added successfully");
        this.name = "";
      },
      err => {
        console.error(err);
        this.showToaster(false, err.message);
      },
      () => console.log("Ladder added")
    )
  }

  showToaster(ok : boolean, message : string){
    if (ok) {
      this.toastr.success(message);
    } else {
      this.toastr.warning(message);
    }
  }
}
