export interface IExercise {
    id?: number;
    minutes?: number;
    activityId?: number;
}

export class Exercise implements IExercise {
    constructor(public id?: number, public minutes?: number, public activityId?: number) {}
}
